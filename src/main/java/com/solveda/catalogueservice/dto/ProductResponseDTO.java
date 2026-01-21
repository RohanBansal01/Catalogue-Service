package com.solveda.catalogueservice.dto;

/**
 * Data Transfer Object (DTO) representing the details of a Product
 * sent from the backend to clients.
 * <p>
 * This DTO is typically returned by service or controller layers
 * to provide a read-only view of product information.
 * </p>
 */
public record ProductResponseDTO(

        /*
          Unique identifier of the product.
         */
        Long id,

        /*
          Name of the product.
         */
        String name,

        /*
          Description of the product.
         */
        String description,

        /*
          Indicates whether the product is active or deactivated.
         */
        boolean active,

        /*
          Identifier of the category to which this product belongs.
         */
        Long categoryId
) {}
