package com.solveda.catalogueservice.dto;

/**
 * Data Transfer Object (DTO) representing a category response
 * returned by the API.
 * <p>
 * Encapsulates information about a category including its identity,
 * title, description, and active status.
 * </p>
 *
 * <p>Typically used in controller responses to send category details
 * to clients after retrieval, creation, or update operations.</p>
 */
public record CategoryResponseDTO(

        /*
         * The unique identifier of the category.
         */
        Long id,

        /*
         * The title of the category.
         */
        String title,

        /**
         * The description of the category.
         */
        String description,

        /**
         * Indicates whether the category is active.
         * <p>
         * True if the category is active, false otherwise.
         * </p>
         */
        boolean active
) {}
