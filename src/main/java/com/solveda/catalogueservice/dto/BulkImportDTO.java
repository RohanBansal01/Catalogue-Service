package com.solveda.catalogueservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing the payload for a bulk import operation.
 * <p>
 * Encapsulates lists of categories and products to be imported in a single request.
 * Provides helper methods for JSON deserialization.
 * </p>
 *
 * <p>Both lists are guaranteed to be non-null after construction. If not provided, they default to empty lists.</p>
 */
public class BulkImportDTO {

    /** List of categories to be imported. */
    @Valid
    private List<CategoryImportDTO> categories;

    /** List of products to be imported. */
    @Valid
    private List<ProductImportDTO> products;

    /**
     * Default constructor.
     * Initializes categories and products to empty lists.
     */
    public BulkImportDTO() {
        this.categories = List.of();
        this.products = List.of();
    }

    /**
     * JSON-creator constructor used by Jackson for deserialization.
     * Ensures lists are non-null.
     *
     * @param categories list of categories to import; defaults to empty list if null
     * @param products list of products to import; defaults to empty list if null
     */
    @JsonCreator
    public BulkImportDTO(
            @JsonProperty("categories") List<CategoryImportDTO> categories,
            @JsonProperty("products") List<ProductImportDTO> products) {
        this.categories = categories != null ? categories : List.of();
        this.products = products != null ? products : List.of();
    }

    /**
     * Returns the list of categories to be imported.
     *
     * @return list of {@link CategoryImportDTO}, never null
     */
    public List<CategoryImportDTO> getCategories() {
        return categories;
    }

    /**
     * Returns the list of products to be imported.
     *
     * @return list of {@link ProductImportDTO}, never null
     */
    public List<ProductImportDTO> getProducts() {
        return products;
    }

    /**
     * Helper method to deserialize a JSON string into a {@code BulkImportDTO}.
     *
     * @param jsonContent JSON content representing categories and products
     * @return deserialized {@code BulkImportDTO} instance
     * @throws IllegalArgumentException if the JSON content is invalid
     */
    public static BulkImportDTO fromJson(String jsonContent) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonContent, BulkImportDTO.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid JSON format: " + ex.getMessage(), ex);
        }
    }
}
