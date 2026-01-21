package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) used to create or update a Product.
 * <p>
 * This DTO carries basic product information required by the service layer,
 * excluding inventory and pricing details, which are handled separately.
 * </p>
 */
public record ProductRequestDTO(

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
          The ID of the category this product belongs to.
          <p>Cannot be null. Must reference an existing Category.</p>
         */
        @NotNull
        Long categoryId
) {}
