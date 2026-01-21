package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.BulkImportDTO;
import com.solveda.catalogueservice.dto.BulkImportResultDTO;
import com.solveda.catalogueservice.service.BulkImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller responsible for handling bulk import operations
 * for categories and products.
 * <p>
 * Supports importing data either via JSON payloads or uploaded JSON files.
 * All operations are delegated to the {@link BulkImportService}.
 * </p>
 */
@RestController
@RequestMapping("/bulk")
@RequiredArgsConstructor
public class BulkImportController {

    /** Service that handles bulk import logic */
    private final BulkImportService bulkImportService;

    /**
     * Imports categories and products from a JSON request body.
     * <p>
     * Expects a {@link BulkImportDTO} containing lists of categories and products.
     * Returns a {@link BulkImportResultDTO} with the number of items imported
     * and any errors or warnings encountered.
     * </p>
     *
     * @param request the bulk import data to process
     * @return response entity containing the result of the import
     */
    @PostMapping("/import-json")
    public ResponseEntity<BulkImportResultDTO> importJson(@RequestBody @Valid BulkImportDTO request) {
        BulkImportResultDTO result = bulkImportService.importData(request);
        return ResponseEntity.ok(result);
    }

    /**
     * Imports categories and products from an uploaded JSON file.
     * <p>
     * Reads the contents of the uploaded file, parses it into a {@link BulkImportDTO},
     * and then processes the import using {@link BulkImportService}.
     * Returns a {@link BulkImportResultDTO} with the outcome.
     * <p>
     * In case of errors during file reading or JSON parsing, returns a
     * {@link ResponseEntity} with HTTP 400 Bad Request and an error message.
     * </p>
     *
     * @param file the uploaded JSON file containing bulk import data
     * @return response entity containing the result of the import or errors
     */
    @PostMapping("/import-file")
    public ResponseEntity<BulkImportResultDTO> importFile(@RequestParam("file") MultipartFile file) {
        try {
            String jsonContent = new String(file.getBytes());
            BulkImportDTO request = BulkImportDTO.fromJson(jsonContent);
            BulkImportResultDTO result = bulkImportService.importData(request);
            return ResponseEntity.ok(result);
        } catch (Exception ex) {
            BulkImportResultDTO errorResult = new BulkImportResultDTO(
                    0, 0,
                    List.of("File processing error: " + ex.getMessage())
            );
            return ResponseEntity.badRequest().body(errorResult);
        }
    }
}
