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

@RestController
@RequestMapping("/bulk")
@RequiredArgsConstructor
public class BulkImportController {

    private final BulkImportService bulkImportService;

    @PostMapping("/import-json")
    public ResponseEntity<BulkImportResultDTO> importJson(@RequestBody @Valid BulkImportDTO request) {
        BulkImportResultDTO result = bulkImportService.importData(request);
        return ResponseEntity.ok(result);
    }

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
