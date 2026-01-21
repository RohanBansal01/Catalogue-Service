package com.solveda.catalogueservice.exception;

/**
 * Thrown when a {@link com.solveda.catalogueservice.model.ProductInventory}
 * operation is invalid, such as reserving more stock than available
 * or releasing more than reserved.
 * <p>
 * Typically raised during stock reservation, release, or adjustment operations.
 * </p>
 */
public class InvalidInventoryOperationException extends RuntimeException {

    /**
     * Constructs a new {@code InvalidInventoryOperationException} with the specified detail message.
     *
     * @param message the detail message describing the invalid inventory operation
     */
    public InvalidInventoryOperationException(String message) {
        super(message);
    }
}
