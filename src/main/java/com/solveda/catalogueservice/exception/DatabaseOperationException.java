package com.solveda.catalogueservice.exception;

/**
 * Thrown when a database operation fails due to connectivity issues,
 * query errors, or other unexpected persistence failures.
 * <p>
 * Typically raised when interacting with the database layer to wrap
 * low-level {@link java.sql.SQLException} or {@link org.springframework.dao.DataAccessException}
 * exceptions.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 * try {
 *     productRepository.save(product);
 * } catch (DataAccessException ex) {
 *     throw new DatabaseOperationException("Failed to save product", ex);
 * }
 * }</pre>
 *
 * @see org.springframework.dao.DataAccessException
 */
public class DatabaseOperationException extends RuntimeException {

    /**
     * Creates a new {@code DatabaseOperationException} with the specified
     * detail message and cause.
     *
     * @param message description of the database operation failure
     * @param cause the underlying cause of the failure
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
