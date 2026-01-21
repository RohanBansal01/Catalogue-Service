package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.ProductInventory;

import java.util.Optional;

/**
 * Service interface for managing product inventory.
 * <p>
 * Handles inventory creation, stock reservation,
 * stock release, and inventory retrieval.
 * </p>
 */
public interface ProductInventoryService {

    /**
     * Creates inventory for a product with an initial quantity.
     *
     * @param productId        product identifier
     * @param initialQuantity initial stock quantity
     * @return the created {@link ProductInventory}
     * @throws RuntimeException if product does not exist or quantity is invalid
     */
    ProductInventory createInventory(Long productId, int initialQuantity);

    /**
     * Reserves stock for a product.
     *
     * @param productId product identifier
     * @param quantity  quantity to reserve
     * @throws RuntimeException if inventory is not found or stock is insufficient
     */
    void reserveStock(Long productId, int quantity);

    /**
     * Releases previously reserved stock.
     *
     * @param productId product identifier
     * @param quantity  quantity to release
     * @throws RuntimeException if inventory is not found or quantity is invalid
     */
    void releaseStock(Long productId, int quantity);

    /**
     * Clears all stock reservations for a product.
     *
     * @param productId product identifier
     * @throws RuntimeException if inventory is not found
     */
    void clearReservations(Long productId);

    /**
     * Retrieves inventory information for a product.
     *
     * @param productId product identifier
     * @return optional containing the inventory if found
     */
    Optional<ProductInventory> getInventory(Long productId);
}
