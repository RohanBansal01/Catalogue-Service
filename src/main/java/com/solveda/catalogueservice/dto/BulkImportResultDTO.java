package com.solveda.catalogueservice.dto;

import java.util.List;

public class BulkImportResultDTO {

    private final int categoriesImported;
    private final int productsImported;
    private final List<String> errors;

    public BulkImportResultDTO(int categoriesImported, int productsImported, List<String> errors) {
        this.categoriesImported = categoriesImported;
        this.productsImported = productsImported;
        this.errors = errors != null ? errors : List.of();
    }

    public int getCategoriesImported() {
        return categoriesImported;
    }

    public int getProductsImported() {
        return productsImported;
    }

    public List<String> getErrors() {
        return errors;
    }
}
