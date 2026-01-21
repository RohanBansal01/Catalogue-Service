package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Data Transfer Object (DTO) representing a request to create or update
 * product inventory.
 * <p>
 * Encapsulates the product identifier and the quantity to be added,
 * reserved, or updated in the inventory system.
 * </p>
 *
 * <p>Used in controller endpoints to receive inventory-related requests
 * from clients.</p>
 */
public record InventoryRequestDTO(

        /*
          The unique identifier of the product whose inventory is being modified.
         */
        @NotNull
        Long productId,

        /*
          The quantity of stock for the product.
          <p>
          Must be zero or a positive number. Negative values are not allowed.
          </p>
         */
        @PositiveOrZero
        int quantity
) {}
