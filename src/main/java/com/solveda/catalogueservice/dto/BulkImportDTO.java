package com.solveda.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

import java.util.List;

public class BulkImportDTO {

    @Valid
    private List<CategoryImportDTO> categories;

    @Valid
    private List<ProductImportDTO> products;

    public BulkImportDTO() {}

    @JsonCreator
    public BulkImportDTO(
            @JsonProperty("categories") List<CategoryImportDTO> categories,
            @JsonProperty("products") List<ProductImportDTO> products) {
        this.categories = categories != null ? categories : List.of();
        this.products = products != null ? products : List.of();
    }

    public List<CategoryImportDTO> getCategories() {
        return categories;
    }

    public List<ProductImportDTO> getProducts() {
        return products;
    }

    public static BulkImportDTO fromJson(String jsonContent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonContent, BulkImportDTO.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JSON format: " + ex.getMessage());
        }
    }
}
