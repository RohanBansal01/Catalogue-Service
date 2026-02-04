package com.solveda.catalogueservice.exception;

/**
 * Exception thrown when a requested product price is not found.
 * <p>
 * This can be used in service or controller layers to indicate
 * that no active price exists for the given product and currency.
 */
public class PriceNotFoundException extends RuntimeException {

    /**
     * Constructs a new {@link PriceNotFoundException} with the specified detail message.
     *
     * @param message the detail message
     */
    public PriceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@link PriceNotFoundException} with the specified detail message
     * and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public PriceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
