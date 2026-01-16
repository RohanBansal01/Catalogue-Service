package com.solveda.catalogueservice.dto;


public record ProductResponseDTO(

        Long id,
        String name,
        String description,
        boolean active,
        Long categoryId
) {
}

