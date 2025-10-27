package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.Category}
 * cannot be found in the system.
 * <p>
 * Typically raised during retrieval, update, or delete operations
 * when a category with the specified identifier does not exist.
 * </p>
 */
public class CategoryNotFoundException extends RuntimeException {

    /**
     * Creates a new {@code CategoryNotFoundException} with the specified detail message.
     *
     * @param message description of the category not found error
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
