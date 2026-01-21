package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) used to request the creation or update of a product's price.
 * <p>
 * Typically used in API endpoints to provide price details for a specific product.
 * </p>
 */
public record PriceRequestDTO(

        /*
          The unique identifier of the product for which the price is being set.
         */
        @NotNull
        Long productId,

        /*
          The ISO 4217 currency code (e.g., "USD", "INR") for the price.
         */
        @NotBlank
        String currency,

        /*
          The monetary amount of the price. Must be positive.
         */
        @NotNull
        @Positive
        BigDecimal amount
) {}
