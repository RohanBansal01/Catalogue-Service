package com.solveda.catalogueservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized structure for all API responses.
 * <p>
 * This generic class provides a unified format for both successful and error responses,
 * improving consistency and simplifying response handling on the client side.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * // Success response
 * ResponseStructure<UserDTO> response = ResponseStructure.<UserDTO>builder()
 *         .status("success")
 *         .data(userDTO)
 *         .build();
 *
 * // Error response
 * ResponseStructure<Object> errorResponse = ResponseStructure.builder()
 *         .status("error")
 *         .error(new ErrorStructure("INVALID_INPUT", "Username is required"))
 *         .build();
 * }</pre>
 * </p>
 *
 * @param <T> the type of data included in the response body
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStructure<T> {

    /**
     * Indicates the response status, typically "success" or "error".
     */
    private String status;

    /**
     * The actual payload returned for successful operations.
     * <p>
     * Will be null if the response represents an error.
     * </p>
     */
    private T data;

    /**
     * Contains error details when the response represents a failure.
     * <p>
     * Will be null if the response represents a successful operation.
     * </p>
     */
    private ErrorStructure error;
}
