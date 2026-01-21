package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a product to be imported in bulk.
 * <p>
 * Used primarily in bulk import operations to create products along with
 * optional stock and pricing information.
 * </p>
 */
public record ProductImportDTO(

        /*
          The name of the product.
          <p>Maximum length: 150 characters. Cannot be blank.</p>
         */
        @NotBlank
        @Size(max = 150)
        String name,

        /*
          The description of the product.
          <p>Maximum length: 500 characters. Optional.</p>
         */
        @Size(max = 500)
        String description,

        /*
          The title of the category this product belongs to.
          <p>Reference by category title. Cannot be blank.</p>
         */
        @NotBlank
        String categoryTitle,

        /*
          The initial stock quantity for the product.
          <p>Must be a positive integer. Optional in some contexts.</p>
         */
        @Positive
        Integer stockQuantity,

        /*
          The price amount for the product.
          <p>Must be positive. Optional in some contexts.</p>
         */
        @Positive
        Double price,

        /*
          The ISO 4217 currency code for the price (e.g., "USD", "INR").
          <p>Cannot be blank. Maximum length: 3 characters.</p>
         */
        @NotBlank
        @Size(max = 3)
        String currency
) {}
