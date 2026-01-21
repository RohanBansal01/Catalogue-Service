package com.solveda.catalogueservice.exception;

/**
 * Exception thrown when an attempt is made to create a {@code Category}
 * with a title that already exists in the system.
 *
 * <p>This is typically used to enforce uniqueness constraints on
 * category titles.</p>
 *
 * <pre>{@code
 * if (categoryRepository.findByTitle(title).isPresent()) {
 *     throw new CategoryAlreadyExistsException(title);
 * }
 * }</pre>
 */
public class CategoryAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new {@code CategoryAlreadyExistsException} with a
     * message indicating the conflicting category title.
     *
     * @param title the title of the category that already exists
     */
    public CategoryAlreadyExistsException(String title) {
        super("Category with title '" + title + "' already exists");
    }
}
