package com.solveda.catalogueservice.dto;


import java.math.BigDecimal;

public record PriceResponseDTO(

        Long id,
        Long productId,
        String currency,
        BigDecimal amount,
        boolean active
) {
}
