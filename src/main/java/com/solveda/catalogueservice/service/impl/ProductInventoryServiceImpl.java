package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidInventoryOperationException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.ProductInventory;
import com.solveda.catalogueservice.repository.ProductInventoryRepository;
import com.solveda.catalogueservice.service.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link ProductInventoryService} for managing product inventory.
 *
 * <p>
 * Provides functionality for:
 * <ul>
 *     <li>Creating inventory for products</li>
 *     <li>Reserving and releasing stock quantities</li>
 *     <li>Clearing reserved stock</li>
 *     <li>Fetching inventory details for a product</li>
 *     <li>Retrieving active inventories in a paginated format</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class handles transactional operations, exception mapping, and validation of
 * inventory operations.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private final ProductInventoryRepository inventoryRepository;

    // =========================
    // CREATE
    // =========================

    /**
     * Creates a new inventory record for the specified product.
     *
     * @param productId       the ID of the product
     * @param initialQuantity the initial stock quantity (must be ≥ 0)
     * @return the saved {@link ProductInventory} entity
     * @throws InvalidInventoryOperationException if the initial quantity is invalid
     * @throws DatabaseOperationException        if there is a database error while saving
     */
    @Override
    public ProductInventory createInventory(Long productId, int initialQuantity) {
        ProductInventory inventory;
        try {
            inventory = ProductInventory.create(productId, initialQuantity);
        } catch (IllegalArgumentException ex) {
            throw new InvalidInventoryOperationException(ex.getMessage());
        }
        return saveInventory(inventory, "creating inventory");
    }

    // =========================
    // STOCK OPERATIONS
    // =========================

    /**
     * Reserves a specified quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to reserve
     * @throws ProductNotFoundException           if the product inventory does not exist
     * @throws InvalidInventoryOperationException if quantity is invalid or insufficient stock
     * @throws DatabaseOperationException        if there is a database error while saving
     */
    @Override
    public void reserveStock(Long productId, int quantity) {
        ProductInventory inventory = findInventoryOrThrow(productId);
        try {
            inventory.reserve(quantity);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new InvalidInventoryOperationException(ex.getMessage());
        }
        saveInventory(inventory, "reserving stock");
    }

    /**
     * Releases a previously reserved quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to release
     * @throws ProductNotFoundException           if the product inventory does not exist
     * @throws InvalidInventoryOperationException if quantity is invalid or exceeds reserved stock
     * @throws DatabaseOperationException        if there is a database error while saving
     */
    @Override
    public void releaseStock(Long productId, int quantity) {
        ProductInventory inventory = findInventoryOrThrow(productId);
        try {
            inventory.release(quantity);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            throw new InvalidInventoryOperationException(ex.getMessage());
        }
        saveInventory(inventory, "releasing stock");
    }

    /**
     * Clears all reserved stock for a product.
     *
     * @param productId the ID of the product
     * @throws ProductNotFoundException    if the product inventory does not exist
     * @throws DatabaseOperationException  if there is a database error while saving
     */
    @Override
    public void clearReservations(Long productId) {
        ProductInventory inventory = findInventoryOrThrow(productId);
        inventory.clearReservations();
        saveInventory(inventory, "clearing reservations");
    }

    // =========================
    // READ
    // =========================

    /**
     * Retrieves the inventory details for a specific product.
     *
     * @param productId the ID of the product
     * @return an {@link Optional} containing the inventory if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductInventory> getInventory(Long productId) {
        return inventoryRepository.findById(productId);
    }

    // =========================
    // PAGINATION
    // =========================

    /**
     * Retrieves all active inventories (available stock > 0) in a paginated format.
     *
     * @param pageable pagination information (page number, size, sort)
     * @return a {@link PaginatedResponseDTO} containing active inventories
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ProductInventory> getAllActiveInventories(Pageable pageable) {
        Page<ProductInventory> page = inventoryRepository.findAllByAvailableQuantityGreaterThan(0, pageable);

        return new PaginatedResponseDTO<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    // =========================
    // INTERNAL HELPERS
    // =========================

    /**
     * Finds inventory by product ID or throws {@link ProductNotFoundException}.
     *
     * @param productId the product ID
     * @return the {@link ProductInventory} entity
     * @throws ProductNotFoundException if the inventory does not exist
     */
    private ProductInventory findInventoryOrThrow(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Inventory for product ID " + productId + " not found"));
    }

    /**
     * Saves the given inventory to the database, wrapping any database exceptions.
     *
     * @param inventory the inventory entity to save
     * @param operation description of the operation (for error messages)
     * @return the saved {@link ProductInventory} entity
     * @throws DatabaseOperationException if a database error occurs
     */
    private ProductInventory saveInventory(ProductInventory inventory, String operation) {
        try {
            return inventoryRepository.save(inventory);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error while " + operation + " for product ID " + inventory.getProductId(), ex);
        }
    }
}
