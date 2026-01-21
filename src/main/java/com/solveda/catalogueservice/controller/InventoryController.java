package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.InventoryRequestDTO;
import com.solveda.catalogueservice.dto.InventoryResponseDTO;
import com.solveda.catalogueservice.model.ProductInventory;
import com.solveda.catalogueservice.service.ProductInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing product inventory.
 * <p>
 * Provides endpoints to create inventory records, reserve stock, release stock,
 * clear reservations, and fetch current inventory for a product.
 * </p>
 *
 * <p>All operations delegate to {@link ProductInventoryService} for business logic and
 * data persistence. Responses are returned as {@link InventoryResponseDTO} for consistent API output.</p>
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    /** Service handling inventory operations */
    private final ProductInventoryService inventoryService;

    /**
     * Creates a new inventory record for a product.
     *
     * @param request {@link InventoryRequestDTO} containing product ID and initial quantity
     * @return {@link ResponseEntity} with status 200 OK and created {@link InventoryResponseDTO}
     */
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> create(@Valid @RequestBody InventoryRequestDTO request) {
        ProductInventory inventory = inventoryService.createInventory(request.productId(), request.quantity());
        return ResponseEntity.ok(toResponse(inventory));
    }

    /**
     * Reserves stock for a product.
     * <p>
     * Decreases available quantity and increases reserved quantity.
     * Idempotency: Not idempotent; calling multiple times reduces stock each time.
     * </p>
     *
     * @param productId the ID of the product
     * @param quantity the quantity to reserve
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long productId, @RequestParam int quantity) {
        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    /**
     * Releases previously reserved stock for a product.
     * <p>
     * Increases available quantity and decreases reserved quantity.
     * Idempotency: Not idempotent; calling multiple times releases stock each time.
     * </p>
     *
     * @param productId the ID of the product
     * @param quantity the quantity to release
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{productId}/release")
    public ResponseEntity<Void> release(@PathVariable Long productId, @RequestParam int quantity) {
        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clears all reservations for a product.
     * <p>
     * Idempotent: Calling multiple times has the same effect as calling once.
     * </p>
     *
     * @param productId the ID of the product
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{productId}/clear")
    public ResponseEntity<Void> clear(@PathVariable Long productId) {
        inventoryService.clearReservations(productId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves the current inventory for a product.
     *
     * @param productId the ID of the product
     * @return {@link ResponseEntity} with status 200 OK and {@link InventoryResponseDTO}
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> get(@PathVariable Long productId) {
        ProductInventory inventory = inventoryService.getInventory(productId)
                .orElseThrow(); // Throws ProductNotFoundException if not present
        return ResponseEntity.ok(toResponse(inventory));
    }

    /**
     * Converts {@link ProductInventory} entity to {@link InventoryResponseDTO}.
     *
     * @param inventory the inventory entity
     * @return DTO representation of the inventory
     */
    private InventoryResponseDTO toResponse(ProductInventory inventory) {
        return new InventoryResponseDTO(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}
