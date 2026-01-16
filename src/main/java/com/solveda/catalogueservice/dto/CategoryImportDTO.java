package com.solveda.catalogueservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryImportDTO(

        @NotBlank
        @Size(max = 100)
        String title,

        @Size(max = 500)
        String description
) {}
