package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidInventoryOperationException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.ProductInventory;
import com.solveda.catalogueservice.repository.ProductInventoryRepository;
import com.solveda.catalogueservice.service.ProductInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementation of {@link ProductInventoryService} that manages product inventory.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Create inventory for products.</li>
 *     <li>Reserve and release stock.</li>
 *     <li>Clear reservations and fetch inventory details.</li>
 *     <li>Handles database operations with proper exception handling.</li>
 * </ul>
 * </p>
 * <p>
 * All write operations are transactional. Read operations are marked
 * {@link Transactional#readOnly()} for optimized performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private final ProductInventoryRepository inventoryRepository;

    /* =========================
       CREATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Creates inventory for a product with the given initial quantity.
     * </p>
     *
     * @throws InvalidInventoryOperationException if validation fails
     * @throws DatabaseOperationException        if saving to database fails
     */
    @Override
    public ProductInventory createInventory(Long productId, int initialQuantity) {
        ProductInventory inventory;
        try {
            inventory = ProductInventory.create(productId, initialQuantity);
        } catch (IllegalArgumentException ex) {
            throw new InvalidInventoryOperationException(ex.getMessage());
        }
        return saveInventory(inventory, "creating");
    }

    /* =========================
       STOCK OPERATIONS
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Reserves stock for a product. Throws an exception if insufficient stock.
     * </p>
     *
     * @throws ProductNotFoundException           if inventory is not found
     * @throws InvalidInventoryOperationException if reservation fails
     * @throws DatabaseOperationException         if saving to database fails
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
     * {@inheritDoc}
     * <p>
     * Releases previously reserved stock.
     * </p>
     *
     * @throws ProductNotFoundException           if inventory is not found
     * @throws InvalidInventoryOperationException if release fails
     * @throws DatabaseOperationException         if saving to database fails
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
     * {@inheritDoc}
     * <p>
     * Clears all reserved stock for the product.
     * </p>
     *
     * @throws ProductNotFoundException   if inventory is not found
     * @throws DatabaseOperationException if saving to database fails
     */
    @Override
    public void clearReservations(Long productId) {
        ProductInventory inventory = findInventoryOrThrow(productId);
        inventory.clearReservations();
        saveInventory(inventory, "clearing reservations");
    }

    /* =========================
       READ
       ========================= */

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductInventory> getInventory(Long productId) {
        return inventoryRepository.findById(productId);
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */

    /**
     * Finds inventory for a product or throws {@link ProductNotFoundException}.
     *
     * @param productId product identifier
     * @return the {@link ProductInventory}
     * @throws ProductNotFoundException if inventory is not found
     */
    private ProductInventory findInventoryOrThrow(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(
                        "Inventory for product ID " + productId + " not found"));
    }

    /**
     * Saves inventory to the database with error handling.
     *
     * @param inventory inventory to save
     * @param operation description of the operation
     * @return saved {@link ProductInventory}
     * @throws DatabaseOperationException if saving fails
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
