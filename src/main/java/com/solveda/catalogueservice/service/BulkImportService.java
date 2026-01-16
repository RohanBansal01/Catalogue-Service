package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.dto.BulkImportDTO;
import com.solveda.catalogueservice.dto.BulkImportResultDTO;

public interface BulkImportService {

    BulkImportResultDTO importData(BulkImportDTO bulkImportDTO);
}
