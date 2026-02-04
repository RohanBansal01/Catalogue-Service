package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.model.ProductInventory;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service interface for managing product inventory.
 *
 * <p>This interface defines operations for:
 * <ul>
 *     <li>Creating inventory records for products.</li>
 *     <li>Reserving and releasing stock quantities.</li>
 *     <li>Clearing reserved stock.</li>
 *     <li>Fetching current inventory details for a product.</li>
 *     <li>Retrieving all active inventory records in a paginated format.</li>
 * </ul>
 * </p>
 *
 * <p>Implementations of this interface should handle transaction management,
 * validation, and exception handling appropriately.</p>
 *
 * @see ProductInventory
 * @see Pageable
 * @see PaginatedResponseDTO
 */
public interface ProductInventoryService {

    /**
     * Creates a new inventory record for a product with the specified initial quantity.
     *
     * @param productId       the ID of the product
     * @param initialQuantity the initial stock quantity
     * @return the created {@link ProductInventory} entity
     * @throws IllegalArgumentException if the initial quantity is negative or invalid
     */
    ProductInventory createInventory(Long productId, int initialQuantity);

    /**
     * Reserves a specific quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to reserve
     * @throws IllegalStateException if there is insufficient available stock
     */
    void reserveStock(Long productId, int quantity);

    /**
     * Releases a previously reserved quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to release
     * @throws IllegalStateException if the quantity to release exceeds the reserved stock
     */
    void releaseStock(Long productId, int quantity);

    /**
     * Clears all reserved stock for a product.
     *
     * @param productId the ID of the product
     */
    void clearReservations(Long productId);

    /**
     * Retrieves the current inventory for a specific product.
     *
     * @param productId the ID of the product
     * @return an {@link Optional} containing {@link ProductInventory} if found, or empty if not found
     */
    Optional<ProductInventory> getInventory(Long productId);

    // =========================
    // Paginated retrieval of active inventories
    // =========================

    /**
     * Retrieves all active inventory records in a paginated format.
     *
     * <p>Active inventories are those with stock available (or marked active).
     * Pagination allows controlling page size, page number, and sorting.</p>
     *
     * <p>Instead of returning a Spring Data Page, this returns a
     * {@link PaginatedResponseDTO} to standardize API output.</p>
     *
     * @param pageable pagination information (page number, size, sort)
     * @return {@link PaginatedResponseDTO} of active {@link ProductInventory} entities
     */
    PaginatedResponseDTO<ProductInventory> getAllActiveInventories(Pageable pageable);
}
