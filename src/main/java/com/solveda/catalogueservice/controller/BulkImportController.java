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
 * REST controller responsible for handling bulk import requests for catalogue data.
 *
 * <p><b>Base Path:</b> {@code /bulk}</p>
 *
 * <p><b>Supported Import Modes</b></p>
 * <ul>
 *   <li><b>JSON Body Import</b> - Accepts a {@link BulkImportDTO} payload directly via request body.</li>
 *   <li><b>File Upload Import</b> - Accepts a JSON file via multipart upload and parses it into {@link BulkImportDTO}.</li>
 * </ul>
 *
 * <p><b>Batch Processing</b></p>
 * <ul>
 *   <li>Supports configurable batch sizes for categories and products.</li>
 *   <li>Batch sizes are validated at controller level to avoid unnecessary service invocation.</li>
 * </ul>
 *
 * <p><b>Validation Strategy</b></p>
 * <ul>
 *   <li>Uses {@link Valid} to trigger Jakarta Bean Validation on {@link BulkImportDTO}.</li>
 *   <li>Batch size parameters must be greater than zero.</li>
 * </ul>
 *
 * <p><b>Response Model</b></p>
 * <ul>
 *   <li>Returns {@link BulkImportResultDTO} containing imported counts and error/warning messages.</li>
 *   <li>Returns HTTP 200 when processing completes successfully (even with partial errors).</li>
 *   <li>Returns HTTP 400 for invalid batch size or file parsing failures.</li>
 * </ul>
 */
@RestController
@RequestMapping("/bulk")
@RequiredArgsConstructor
public class BulkImportController {

    /**
     * Default category batch size used when request parameter is not provided.
     */
    private static final int DEFAULT_CATEGORY_BATCH_SIZE = 10;

    /**
     * Default product batch size used when request parameter is not provided.
     */
    private static final int DEFAULT_PRODUCT_BATCH_SIZE = 100;

    private final BulkImportService bulkImportService;

    /**
     * Imports catalogue data using a JSON request body.
     *
     * <p><b>Endpoint:</b> {@code POST /bulk/import-json}</p>
     *
     * <p>Validates batch size parameters before invoking the import service.</p>
     *
     * @param request           validated bulk import request containing categories and products.
     * @param categoryBatchSize maximum number of categories processed per transaction batch.
     * @param productBatchSize  maximum number of products processed per transaction batch.
     * @return HTTP 200 with import result on success, HTTP 400 if batch sizes are invalid.
     */
    @PostMapping("/import-json")
    public ResponseEntity<BulkImportResultDTO> importJson(
            @RequestBody @Valid BulkImportDTO request,
            @RequestParam(defaultValue = "" + DEFAULT_CATEGORY_BATCH_SIZE) int categoryBatchSize,
            @RequestParam(defaultValue = "" + DEFAULT_PRODUCT_BATCH_SIZE) int productBatchSize
    ) {

        if (categoryBatchSize <= 0) {
            return ResponseEntity.badRequest().body(
                    new BulkImportResultDTO(0, 0,
                            List.of("Invalid categoryBatchSize: must be greater than 0"))
            );
        }

        if (productBatchSize <= 0) {
            return ResponseEntity.badRequest().body(
                    new BulkImportResultDTO(0, 0,
                            List.of("Invalid productBatchSize: must be greater than 0"))
            );
        }

        BulkImportResultDTO result =
                bulkImportService.importData(request, categoryBatchSize, productBatchSize);

        return ResponseEntity.ok(result);
    }

    /**
     * Imports catalogue data using a multipart JSON file upload.
     *
     * <p><b>Endpoint:</b> {@code POST /bulk/import-file}</p>
     *
     * <p>Reads the file content as a JSON string, converts it into {@link BulkImportDTO},
     * and delegates processing to {@link BulkImportService}.</p>
     *
     * <p>Returns HTTP 400 for:</p>
     * <ul>
     *   <li>Invalid batch size parameters</li>
     *   <li>File read failures</li>
     *   <li>Invalid JSON format</li>
     * </ul>
     *
     * @param file              uploaded JSON file containing catalogue data.
     * @param categoryBatchSize maximum number of categories processed per transaction batch.
     * @param productBatchSize  maximum number of products processed per transaction batch.
     * @return HTTP 200 with import result if parsing + import succeeds, HTTP 400 otherwise.
     */
    @PostMapping("/import-file")
    public ResponseEntity<BulkImportResultDTO> importFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "" + DEFAULT_CATEGORY_BATCH_SIZE) int categoryBatchSize,
            @RequestParam(defaultValue = "" + DEFAULT_PRODUCT_BATCH_SIZE) int productBatchSize
    ) {

        if (categoryBatchSize <= 0) {
            return ResponseEntity.badRequest().body(
                    new BulkImportResultDTO(0, 0,
                            List.of("Invalid categoryBatchSize: must be greater than 0"))
            );
        }

        if (productBatchSize <= 0) {
            return ResponseEntity.badRequest().body(
                    new BulkImportResultDTO(0, 0,
                            List.of("Invalid productBatchSize: must be greater than 0"))
            );
        }

        try {
            String jsonContent = new String(file.getBytes());
            BulkImportDTO request = BulkImportDTO.fromJson(jsonContent);

            BulkImportResultDTO result =
                    bulkImportService.importData(request, categoryBatchSize, productBatchSize);

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