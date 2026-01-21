package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.ProductPrice}
 * fails validation due to invalid amount or currency.
 * <p>
 * Typically raised during price creation or update when
 * business rules for price amounts or currency codes are violated.
 * </p>
 */
public class InvalidPriceException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidPriceException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidPriceException(String message) {
        super(message);
    }
}
