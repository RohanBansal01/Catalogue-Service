package com.solveda.catalogueservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductRequestDTO(

        @NotBlank
        @Size(max = 150)
        String name,

        @Size(max = 500)
        String description,

        @NotNull
        Long categoryId
) {
}
