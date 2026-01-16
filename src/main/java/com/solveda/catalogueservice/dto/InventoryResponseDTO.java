package com.solveda.catalogueservice.dto;


public record InventoryResponseDTO(

        Long productId,
        int availableQuantity,
        int reservedQuantity
) {
}
