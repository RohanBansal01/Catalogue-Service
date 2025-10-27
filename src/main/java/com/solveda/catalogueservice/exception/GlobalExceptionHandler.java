package com.solveda.catalogueservice.exception;

import com.solveda.catalogueservice.payload.ErrorStructure;
import com.solveda.catalogueservice.payload.ResponseStructure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Global exception handler that intercepts exceptions thrown in the application
 * and converts them into consistent, structured API responses using
 * {@link ResponseStructure} and {@link ErrorStructure}.
 * <p>
 * This class ensures that clients receive clear, uniform error messages
 * with relevant HTTP status codes, error codes, and logical sources.
 * </p>
 *
 * <p>Exception types handled include:</p>
 * <ul>
 *     <li>{@link ProductNotFoundException} → 404 Not Found</li>
 *     <li>{@link InvalidProductException} → 400 Bad Request</li>
 *     <li>{@link DatabaseOperationException} → 500 Internal Server Error</li>
 *     <li>{@link SQLIntegrityConstraintViolationException} → 409 Conflict</li>
 *     <li>{@link DataAccessException} → 500 Internal Server Error</li>
 *     <li>{@link MethodArgumentNotValidException} → 400 Bad Request</li>
 *     <li>{@link IllegalArgumentException} → 400 Bad Request</li>
 *     <li>All other {@link Exception} → 500 Internal Server Error</li>
 * </ul>
 *
 * <p>Logging is included for unexpected errors to aid debugging, while
 * the response sent to clients remains clean and structured.</p>
 *
 * @see ResponseStructure
 * @see ErrorStructure
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Builds a standardized error response for any exception.
     *
     * @param status  the HTTP status to return
     * @param message a human-readable description of the error
     * @param code    a unique error code for categorizing the error
     * @param source  the logical source or layer of the error
     * @return a {@link ResponseEntity} containing the structured error
     */
    private ResponseEntity<ResponseStructure<?>> buildErrorResponse(
            HttpStatus status, String message, String code, String source) {
        ErrorStructure error = ErrorStructure.builder()
                .errorCode(code)
                .errorMessage(message)
                .errorSource(source)
                .build();

        ResponseStructure<?> response = ResponseStructure.builder()
                .status("error")
                .data(null)
                .error(error)
                .build();

        return ResponseEntity.status(status).body(response);
    }
    /**
     * Handles {@link ProductNotFoundException} and returns a 404 Not Found
     * response with a structured error.
     *
     * @param ex the thrown ProductNotFoundException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseStructure<?>> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "PRODUCT_NOT_FOUND", "ProductService");
    }

    /**
     * Handles {@link InvalidProductException} and returns a 400 Bad Request
     * response with a structured error.
     *
     * @param ex the thrown InvalidProductException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidProduct(InvalidProductException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_PRODUCT_DATA", "ProductValidation");
    }

    /**
     * Handles {@link DatabaseOperationException} and returns a 500 Internal Server Error
     * response with a structured error.
     *
     * @param ex the thrown DatabaseOperationException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ResponseStructure<?>> handleDatabaseError(DatabaseOperationException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), "DATABASE_ERROR", "DatabaseLayer");
    }


    /**
     * Handles {@link SQLIntegrityConstraintViolationException} and returns a 409 Conflict
     * response with a structured error.
     *
     * @param ex the thrown SQLIntegrityConstraintViolationException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseStructure<?>> handleSQLConstraint(SQLIntegrityConstraintViolationException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, "Database constraint violation: " + ex.getMessage(),
                "DB_CONSTRAINT_ERROR", "DatabaseLayer");
    }

    /**
     * Handles {@link DataAccessException} and returns a 500 Internal Server Error
     * response with a structured error.
     *
     * @param ex the thrown DataAccessException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseStructure<?>> handleDataAccess(DataAccessException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Database access error: " + ex.getMessage(),
                "DB_ACCESS_ERROR", "DatabaseLayer");
    }

    /**
     * Handles {@link MethodArgumentNotValidException} and returns a 400 Bad Request
     * response with validation error details.
     *
     * @param ex the thrown MethodArgumentNotValidException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStructure<?>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + message,
                "VALIDATION_ERROR", "Validation");
    }

    /**
     * Handles {@link IllegalArgumentException} and returns a 400 Bad Request
     * response with a structured error.
     *
     * @param ex the thrown IllegalArgumentException
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseStructure<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request: " + ex.getMessage(),
                "BAD_REQUEST", "RequestValidation");
    }

    /**
     * Handles {@link CategoryNotFoundException} and returns a 404 Not Found
     * response with a structured error message.
     * <p>
     *     Triggered when a requested {@link com.solveda.catalogueservice.model.Category}
     *     cannot be found in the database during retrieval, update, or deletion operations.
     * </p>
     *
     * <p><b>Example scenario:</b></p>
     * <ul>
     *     <li>Fetching a category by ID or name that does not exist.</li>
     *     <li>Updating a category record that has been deleted or not created yet.</li>
     * </ul>
     *
     * @param ex the thrown {@code CategoryNotFoundException}
     * @return a {@link ResponseEntity} containing a {@link ResponseStructure} with error details,
     *         status {@code 404 NOT_FOUND}, and error code {@code CATEGORY_NOT_FOUND}
     *
     * @see com.solveda.catalogueservice.exception.CategoryNotFoundException
     * @see com.solveda.catalogueservice.service.CategoryService
     */
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ResponseStructure<?>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), "CATEGORY_NOT_FOUND", "CategoryService");
    }

    /**
     * Handles {@link InvalidCategoryException} and returns a 400 Bad Request
     * response with structured validation error details.
     * <p>
     *     Triggered when a {@link com.solveda.catalogueservice.model.Category}
     *     fails validation checks such as missing required fields, invalid values,
     *     or violations of business rules during creation or update.
     * </p>
     *
     * <p><b>Example scenario:</b></p>
     * <ul>
     *     <li>Blank or null category name.</li>
     *     <li>Invalid parent category reference.</li>
     *     <li>Duplicate category name where uniqueness is required.</li>
     * </ul>
     *
     * @param ex the thrown {@code InvalidCategoryException}
     * @return a {@link ResponseEntity} containing a {@link ResponseStructure} with error details,
     *         status {@code 400 BAD_REQUEST}, and error code {@code INVALID_CATEGORY_DATA}
     *
     * @see com.solveda.catalogueservice.exception.InvalidCategoryException
     */
    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidCategory(InvalidCategoryException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "INVALID_CATEGORY_DATA", "CategoryValidation");
    }

    /**
     * Handles all uncaught {@link Exception} instances and returns a 500 Internal Server Error
     * response with a structured error. Also logs the exception for debugging.
     *
     * @param ex the thrown Exception
     * @return ResponseEntity with error details in {@link ResponseStructure}
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<?>> handleGeneric(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage(),
                "INTERNAL_ERROR", "GlobalExceptionHandler");
    }
}
