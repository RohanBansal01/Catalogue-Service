package com.solveda.catalogueservice.dto;

import java.util.List;

/**
 * Represents the result of a bulk import operation for categories and products.
 * <p>
 * This DTO encapsulates the summary of the import process including:
 * <ul>
 *     <li>The number of categories successfully imported</li>
 *     <li>The number of products successfully imported</li>
 *     <li>A list of errors or warnings encountered during the import</li>
 * </ul>
 * </p>
 *
 * <p>Errors list is guaranteed to be non-null. If no errors occurred, it will be an empty list.</p>
 */
public class BulkImportResultDTO {

    /** Number of categories successfully imported during the bulk operation. */
    private final int categoriesImported;

    /** Number of products successfully imported during the bulk operation. */
    private final int productsImported;

    /** List of error or warning messages generated during the import process. */
    private final List<String> errors;

    /**
     * Constructs a new {@code BulkImportResultDTO} with the specified counts and error messages.
     *
     * @param categoriesImported number of categories successfully imported
     * @param productsImported number of products successfully imported
     * @param errors list of errors or warnings encountered; if null, will default to empty list
     */
    public BulkImportResultDTO(int categoriesImported, int productsImported, List<String> errors) {
        this.categoriesImported = categoriesImported;
        this.productsImported = productsImported;
        this.errors = errors != null ? errors : List.of();
    }

    /**
     * Returns the number of categories successfully imported.
     *
     * @return count of imported categories
     */
    public int getCategoriesImported() {
        return categoriesImported;
    }

    /**
     * Returns the number of products successfully imported.
     *
     * @return count of imported products
     */
    public int getProductsImported() {
        return productsImported;
    }

    /**
     * Returns the list of errors or warnings encountered during the import.
     * <p>
     * This list is guaranteed to be non-null but may be empty if no issues occurred.
     * </p>
     *
     * @return list of error or warning messages
     */
    public List<String> getErrors() {
        return errors;
    }
}
