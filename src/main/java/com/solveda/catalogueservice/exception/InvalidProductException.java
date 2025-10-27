package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.Product}
 * fails validation due to missing or invalid attributes.
 * <p>
 *     Typically raised during product creation or update when
 *     business or data integrity rules are violated.
 * </p>
 *
 * <p><b>Examples:</b></p>
 * <ul>
 *     <li>Blank name or description</li>
 *     <li>Negative or null price/stock</li>
 *     <li>Blank SKU or null active status</li>
 * </ul>
 *
 * @see com.solveda.catalogueservice.service.impl.ProductServiceImpl
 */
public class InvalidProductException extends RuntimeException {
    /**
     * Constructs a new {@code InvalidProductException} with the specified detail message.
     *
     * @param message the detail message describing the validation failure
     */
    public InvalidProductException(String message){
        super(message);
    }
}
