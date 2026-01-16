package com.solveda.catalogueservice.dto;


public record CategoryResponseDTO(

        Long id,
        String title,
        String description,
        boolean active
) {
}
