package com.solveda.catalogueservice.controller;


import com.solveda.catalogueservice.dto.InventoryRequestDTO;
import com.solveda.catalogueservice.dto.InventoryResponseDTO;
import com.solveda.catalogueservice.model.ProductInventory;
import com.solveda.catalogueservice.service.ProductInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final ProductInventoryService inventoryService;

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> create(
            @Valid @RequestBody InventoryRequestDTO request) {

        ProductInventory inventory =
                inventoryService.createInventory(
                        request.productId(),
                        request.quantity()
                );

        return ResponseEntity.ok(toResponse(inventory));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Void> reserve(
            @PathVariable Long productId,
            @RequestParam int quantity) {

        inventoryService.reserveStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<Void> release(
            @PathVariable Long productId,
            @RequestParam int quantity) {

        inventoryService.releaseStock(productId, quantity);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{productId}/clear")
    public ResponseEntity<Void> clear(@PathVariable Long productId) {
        inventoryService.clearReservations(productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponseDTO> get(@PathVariable Long productId) {
        ProductInventory inventory =
                inventoryService.getInventory(productId).orElseThrow();

        return ResponseEntity.ok(toResponse(inventory));
    }

    private InventoryResponseDTO toResponse(ProductInventory inventory) {
        return new InventoryResponseDTO(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity()
        );
    }
}
