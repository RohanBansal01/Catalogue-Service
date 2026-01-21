package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) representing a category request
 * for creating or updating a category via API.
 * <p>
 * This record enforces basic validation rules for category data:
 * <ul>
 *     <li>{@code title} is mandatory and cannot be blank, with a maximum length of 100 characters.</li>
 *     <li>{@code description} is optional, with a maximum length of 500 characters.</li>
 * </ul>
 * </p>
 *
 * <p>Typically used in controller endpoints to receive input for category creation
 * or updates from clients.</p>
 */
public record CategoryRequestDTO(

        /*
          The title of the category.
          <p>Must not be blank and cannot exceed 100 characters.</p>
         */
        @NotBlank
        @Size(max = 100)
        String title,

        /*
          The description of the category.
          <p>Optional field with a maximum length of 500 characters.</p>
         */
        @Size(max = 500)
        String description
) {}
