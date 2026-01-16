package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.ProductInventory;

import java.util.Optional;

public interface ProductInventoryService {

    ProductInventory createInventory(Long productId, int initialQuantity);

    void reserveStock(Long productId, int quantity);

    void releaseStock(Long productId, int quantity);

    void clearReservations(Long productId);

    Optional<ProductInventory> getInventory(Long productId);
}
