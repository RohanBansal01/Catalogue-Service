package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.Product}
 * fails validation due to missing or invalid attributes.
 * <p>
 * Typically raised during product creation or update when
 * business or data integrity rules are violated.
 * </p>
 */
public class InvalidProductException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidProductException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidProductException(String message) {
        super(message);
    }
}
