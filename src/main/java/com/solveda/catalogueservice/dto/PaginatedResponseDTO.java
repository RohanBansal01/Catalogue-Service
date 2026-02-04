package com.solveda.catalogueservice.dto;

import java.util.List;

/**
 * Standardized DTO for paginated API responses.
 * <p>
 * Wraps paginated content and provides client-friendly metadata
 * without exposing Spring Data {@code Page} internals.
 * Useful for REST endpoints to return consistent pagination information.
 *
 * @param <T> the type of content in the page
 */
public record PaginatedResponseDTO<T>(
        /*
         * The list of items in the current page.
         */
        List<T> content,

        /*
         * The current page number (0-based).
         */
        int page,

        /*
         * The size of the page (number of items per page).
         */
        int size,

        /*
         * Total number of elements across all pages.
         */
        long totalElements,

        /*
         * Total number of pages available.
         */
        int totalPages,

        /*
         * Flag indicating if this is the first page.
         */
        boolean first,

        /*
         * Flag indicating if this is the last page.
         */
        boolean last
) {}
