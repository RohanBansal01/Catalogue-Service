package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.dto.BulkImportDTO;
import com.solveda.catalogueservice.dto.BulkImportResultDTO;

/**
 * Service interface for bulk import operations.
 * Handles validation and processing of bulk catalogue imports.
 */
public interface BulkImportService {

    /**
     * Imports catalogue data in bulk using batch processing.
     *
     * @param bulkImportDTO request payload
     * @param categoryBatchSize batch size for categories (must be > 0)
     * @param productBatchSize batch size for products (must be > 0)
     * @return import result summary
     */
    BulkImportResultDTO importData(BulkImportDTO bulkImportDTO, int categoryBatchSize, int productBatchSize);
}