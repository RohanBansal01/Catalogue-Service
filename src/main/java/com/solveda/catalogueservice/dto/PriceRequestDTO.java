package com.solveda.catalogueservice.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PriceRequestDTO(

        @NotNull
        Long productId,

        @NotBlank
        String currency,

        @NotNull
        @Positive
        BigDecimal amount
) {
}
