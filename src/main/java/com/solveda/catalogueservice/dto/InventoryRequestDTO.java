package com.solveda.catalogueservice.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequestDTO(

        @NotNull
        Long productId,

        @PositiveOrZero
        int quantity
) {
}
