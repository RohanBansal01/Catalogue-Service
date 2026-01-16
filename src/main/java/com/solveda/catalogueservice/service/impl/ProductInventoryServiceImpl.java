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

@Service
@RequiredArgsConstructor
@Transactional
public class ProductInventoryServiceImpl implements ProductInventoryService {

    private final ProductInventoryRepository inventoryRepository;

    /* =========================
       CREATE
       ========================= */
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

    @Override
    public void clearReservations(Long productId) {
        ProductInventory inventory = findInventoryOrThrow(productId);
        inventory.clearReservations();
        saveInventory(inventory, "clearing reservations");
    }

    /* =========================
       READ
       ========================= */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductInventory> getInventory(Long productId) {
        return inventoryRepository.findById(productId);
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */
    private ProductInventory findInventoryOrThrow(Long productId) {
        return inventoryRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Inventory for product ID " + productId + " not found"));
    }

    private ProductInventory saveInventory(ProductInventory inventory, String operation) {
        try {
            return inventoryRepository.save(inventory);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while " + operation + " for product ID " + inventory.getProductId(), ex);
        }
    }
}
