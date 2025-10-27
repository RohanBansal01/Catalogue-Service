package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.Category}
 * fails validation due to missing or invalid attributes.
 * <p>
 * Typically raised during category creation or update when
 * business or data integrity rules are violated.
 * </p>
 */
public class InvalidCategoryException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidCategoryException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidCategoryException(String message) {
        super(message);
    }
}
