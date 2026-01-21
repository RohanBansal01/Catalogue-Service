package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.dto.BulkImportDTO;
import com.solveda.catalogueservice.dto.BulkImportResultDTO;

/**
 * Service interface for bulk import operations.
 * <p>
 * Handles validation and processing of bulk data imports
 * for catalogue entities.
 * </p>
 */
public interface BulkImportService {

    /**
     * Imports catalogue data in bulk.
     *
     * @param bulkImportDTO bulk import request data
     * @return result of the bulk import operation
     * @throws RuntimeException if import validation or processing fails
     */
    BulkImportResultDTO importData(BulkImportDTO bulkImportDTO);
}
