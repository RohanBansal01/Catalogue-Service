package com.solveda.catalogueservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized structure for error details returned in API responses.
 * <p>
 * This class encapsulates information about the nature, description, and origin
 * of an error, ensuring consistent error reporting across the application.
 * </p>
 *
 * <p><b>Typical Usage:</b></p>
 * <pre>{@code
 * ErrorStructure error = ErrorStructure.builder()
 *     .errorCode("PRODUCT_NOT_FOUND")
 *     .errorMessage("Product with ID 101 not found")
 *     .errorSource("ProductService")
 *     .build();
 * }</pre>
 *
 * <p>
 * Used within {@link com.solveda.catalogueservice.payload.ResponseStructure}
 * to build structured error responses.
 * </p>
 *
 * @see com.solveda.catalogueservice.payload.ResponseStructure
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorStructure {

    /**
     * A unique code representing the type or category of the error.
     * <p>Example: {@code PRODUCT_NOT_FOUND}, {@code DATABASE_ERROR}</p>
     */
    private String errorCode;

    /**
     * A human-readable message describing the error in detail.
     * <p>Example: {@code "Product with ID 101 not found"}</p>
     */
    private String errorMessage;

    /**
     * The logical source or layer where the error originated.
     * <p>Example: {@code "ProductService"}, {@code "DatabaseLayer"}</p>
     */
    private String errorSource;
}
