package com.solveda.catalogueservice.exception;

/**
 * Exception thrown when a {@link com.solveda.catalogueservice.model.Category}
 * cannot be found in the system.
 *
 * <p>This exception is typically raised during retrieval, update, or delete
 * operations when a category with the specified identifier or title does not exist.</p>
 *
 * <pre>{@code
 * Category category = categoryRepository.findById(id)
 *     .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
 * }</pre>
 */
public class CategoryNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@code CategoryNotFoundException} with the specified detail message.
     *
     * @param message a descriptive message explaining why the category was not found
     */
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
