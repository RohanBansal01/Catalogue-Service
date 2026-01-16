package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProductImportDTO(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotBlank
        String categoryTitle, // reference by category title

        @Positive
        Integer stockQuantity,

        @Positive
        Double price,

        @NotBlank
        @Size(max = 3)
        String currency
) {}
