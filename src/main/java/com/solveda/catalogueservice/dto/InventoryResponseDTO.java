package com.solveda.catalogueservice.dto;

/**
 * Data Transfer Object (DTO) representing the current state of a product's inventory.
 * <p>
 * Provides information about the available stock and reserved quantity for a specific product.
 * Typically returned from inventory-related API endpoints.
 * </p>
 */
public record InventoryResponseDTO(

        /**
         * The unique identifier of the product.
         */
        Long productId,

        /*
          The number of units currently available for sale or reservation.
         */
        int availableQuantity,

        /*
          The number of units currently reserved and not available for sale.
         */
        int reservedQuantity
) {}
