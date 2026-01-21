package com.solveda.catalogueservice.exception;

/**
 * Exception thrown when a database operation fails due to connectivity issues,
 * query errors, or other unexpected persistence failures.
 *
 * <p>This exception is typically used to wrap low-level {@link java.sql.SQLException}
 * or {@link org.springframework.dao.DataAccessException} when interacting with
 * the persistence layer.</p>
 *
 * <p><b>Example usage:</b></p>
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
     * Constructs a new {@code DatabaseOperationException} with the specified
     * detail message and cause.
     *
     * @param message descriptive message explaining the failure
     * @param cause the underlying exception that triggered this failure
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
