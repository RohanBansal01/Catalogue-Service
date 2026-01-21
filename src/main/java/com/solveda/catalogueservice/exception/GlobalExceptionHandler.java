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
 * Global exception handler for the Catalogue Service API.
 *
 * <p>
 * This class intercepts exceptions thrown by controllers or service layers and
 * converts them into standardized {@link ResponseStructure} responses.
 * It ensures consistent error reporting, structured payloads, and
 * appropriate HTTP status codes for REST endpoints.
 * </p>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>Handles custom domain exceptions like ProductNotFound, InvalidProduct, etc.</li>
 *     <li>Wraps database exceptions and validation errors.</li>
 *     <li>Provides a catch-all handler for unexpected exceptions.</li>
 *     <li>Uses {@link ErrorStructure} to standardize error details.</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Builds a standardized error response with HTTP status, message, code, and source.
     *
     * @param status  HTTP status to return
     * @param message Human-readable error message
     * @param code    Unique error code
     * @param source  Logical source or layer of the error
     * @return {@link ResponseEntity} containing a structured {@link ResponseStructure}
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

    // =========================
    // Product Exceptions
    // =========================

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ResponseStructure<?>> handleProductNotFound(ProductNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(),
                "PRODUCT_NOT_FOUND", "ProductService");
    }

    @ExceptionHandler(InvalidProductException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidProduct(InvalidProductException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                "INVALID_PRODUCT_DATA", "ProductValidation");
    }

    // =========================
    // Inventory Exceptions
    // =========================

    @ExceptionHandler(InvalidInventoryOperationException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidInventory(InvalidInventoryOperationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                "INVALID_INVENTORY_OPERATION", "InventoryService");
    }

    // =========================
    // Price Exceptions
    // =========================

    @ExceptionHandler(InvalidPriceException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidPrice(InvalidPriceException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                "INVALID_PRICE_DATA", "PriceService");
    }

    // =========================
    // Category Exceptions
    // =========================

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ResponseStructure<?>> handleCategoryNotFound(CategoryNotFoundException ex) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(),
                "CATEGORY_NOT_FOUND", "CategoryService");
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ResponseEntity<ResponseStructure<?>> handleInvalidCategory(InvalidCategoryException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(),
                "INVALID_CATEGORY_DATA", "CategoryValidation");
    }

    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ResponseStructure<?>> handleCategoryAlreadyExists(CategoryAlreadyExistsException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(),
                "CATEGORY_ALREADY_EXISTS", "CategoryService");
    }

    // =========================
    // Database Exceptions
    // =========================

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ResponseStructure<?>> handleDatabaseError(DatabaseOperationException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),
                "DATABASE_ERROR", "DatabaseLayer");
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseStructure<?>> handleSQLConstraint(SQLIntegrityConstraintViolationException ex) {
        return buildErrorResponse(HttpStatus.CONFLICT,
                "Database constraint violation: " + ex.getMessage(),
                "DB_CONSTRAINT_ERROR", "DatabaseLayer");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ResponseStructure<?>> handleDataAccess(DataAccessException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Database access error: " + ex.getMessage(),
                "DB_ACCESS_ERROR", "DatabaseLayer");
    }

    // =========================
    // Validation Exceptions
    // =========================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStructure<?>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldError().getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + message,
                "VALIDATION_ERROR", "Validation");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseStructure<?>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request: " + ex.getMessage(),
                "BAD_REQUEST", "RequestValidation");
    }

    // =========================
    // Catch-All
    // =========================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<?>> handleGeneric(Exception ex) {
        logger.error("Unexpected error occurred: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error: " + ex.getMessage(),
                "INTERNAL_ERROR", "GlobalExceptionHandler");
    }
}
