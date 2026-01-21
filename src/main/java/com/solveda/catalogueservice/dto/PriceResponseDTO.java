package com.solveda.catalogueservice.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing the response details of a product price.
 * <p>
 * Typically returned by API endpoints after creating, retrieving, or updating a price.
 * </p>
 */
public record PriceResponseDTO(

        /*
          The unique identifier of the price record.
         */
        Long id,

        /*
          The unique identifier of the product associated with this price.
         */
        Long productId,

        /*
          The ISO 4217 currency code (e.g., "USD", "INR") for the price.
         */
        String currency,

        /*
          The monetary amount of the price.
         */
        BigDecimal amount,

        /*
          Indicates whether the price is currently active.
         */
        boolean active
) {}
