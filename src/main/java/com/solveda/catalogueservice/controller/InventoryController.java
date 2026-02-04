package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.InventoryRequestDTO;
import com.solveda.catalogueservice.dto.InventoryResponseDTO;
import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.model.ProductInventory;
import com.solveda.catalogueservice.service.ProductInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing product inventory.
 *
 * <p>
 * Provides endpoints for:
 * <ul>
 *     <li>Creating inventory records for products.</li>
 *     <li>Reserving and releasing stock quantities.</li>
 *     <li>Clearing reserved stock.</li>
 *     <li>Fetching inventory details for a product.</li>
 *     <li>Retrieving all inventories in a paginated format.</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductInventoryService inventoryService;

    // =========================
    // CREATE / STOCK OPERATIONS
    // =========================

    /**
     * Creates a new inventory record for a product.
     *
     * @param request the inventory creation request containing product ID and initial quantity
     * @return the created inventory details
     */
    @PostMapping
    public ResponseEntity<InventoryResponseDTO> create(@Valid @RequestBody InventoryRequestDTO request) {
        ProductInventory inventory = inventoryService.createInventory(request.productId(), request.quantity());
        return ResponseEntity.ok(toResponse(inventory));
    }

    /**
     * Reserves a specific quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to reserve
     * @return HTTP 204 No Content on success
     */
    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserve(@PathVariable Long productId, @RequestParam int quantity) {
        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    /**
     * Releases a previously reserved quantity of stock for a product.
     *
     * @param productId the ID of the product
     * @param quantity  the quantity to release
     * @return HTTP 204 No Content on success
     */
    @PostMapping("/{productId}/release")
    public ResponseEntity<Void> release(@PathVariable Long productId, @RequestParam int quantity) {
        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    /**
     * Clears all reserved stock for a product.
     *
     * @param productId the ID of the product
     * @return HTTP 204 No Content on success
     */
    @PostMapping("/{productId}/clear")
    public ResponseEntity<Void> clear(@PathVariable Long productId) {
        inventoryService.clearReservations(productId);
        return ResponseEntity.noContent().build();
    }

    // =========================
    // GET INVENTORY
    // =========================

    /**
     * Retrieves the current inventory for a specific product.
     *
     * @param productId the ID of the product
     * @return inventory details for the product
     * @throws RuntimeException (or a custom ProductNotFoundException) if inventory not found
     */
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> get(@PathVariable Long productId) {
        ProductInventory inventory = inventoryService.getInventory(productId)
                .orElseThrow(() -> new RuntimeException("Product inventory not found for productId: " + productId));
        return ResponseEntity.ok(toResponse(inventory));
    }

    // =========================
    // PAGINATED GET
    // =========================

    /**
     * Retrieves all active inventories in a paginated format.
     *
     * <p>Active inventories are those with stock available.</p>
     *
     * @param pageable pagination information (page number, size, sort)
     * @return paginated response containing inventory details
     */
    @GetMapping("/active")
    public ResponseEntity<PaginatedResponseDTO<InventoryResponseDTO>> getAllActiveInventories(Pageable pageable) {
        PaginatedResponseDTO<ProductInventory> paginatedInventory = inventoryService.getAllActiveInventories(pageable);
        return ResponseEntity.ok(mapPaginatedResponse(paginatedInventory));
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Converts a {@link ProductInventory} entity to {@link InventoryResponseDTO}.
     *
     * @param inventory the inventory entity
     * @return the response DTO
     */
    private InventoryResponseDTO toResponse(ProductInventory inventory) {
        return new InventoryResponseDTO(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }

    /**
     * Maps a paginated response of {@link ProductInventory} entities to {@link InventoryResponseDTO} objects.
     *
     * @param source the original paginated response of ProductInventory
     * @return a new {@link PaginatedResponseDTO} containing InventoryResponseDTO content
     */
    private PaginatedResponseDTO<InventoryResponseDTO> mapPaginatedResponse(
            PaginatedResponseDTO<ProductInventory> source) {
        return new PaginatedResponseDTO<>(
                source.content().stream().map(this::toResponse).toList(),
                source.page(),
                source.size(),
                source.totalElements(),
                source.totalPages(),
                source.first(),
                source.last()
        );
    }
}
