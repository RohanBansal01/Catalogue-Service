package com.solveda.catalogueservice.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a standardized structure for all API responses.
 * <p>
 * This class provides a unified format for both successful and error responses,
 * improving consistency and simplifying response handling on the client side.
 * </p>
 *
 * @param <T> the type of data included in the response body
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStructure<T> {

    /** Indicates the response status, e.g., "success" or "error". */
    private String status;

    /** The actual payload returned for successful operations. */
    private T data;

    /** Contains error details when the response represents a failure. */
    private ErrorStructure error;
}
