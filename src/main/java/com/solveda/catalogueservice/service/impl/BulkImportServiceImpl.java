package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.dto.*;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidCategoryException;
import com.solveda.catalogueservice.exception.InvalidProductException;
import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportServiceImpl implements BulkImportService {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductInventoryService inventoryService;
    private final ProductPriceService priceService;

    @Override
    public BulkImportResultDTO importData(BulkImportDTO bulkImportDTO) {

        List<String> validationErrors = new ArrayList<>();
        List<String> databaseErrors = new ArrayList<>();
        List<String> duplicateWarnings = new ArrayList<>();
        int categoriesImported = 0;
        int productsImported = 0;

        // -------------------------
        // Import Categories
        // -------------------------
        for (CategoryImportDTO c : bulkImportDTO.getCategories()) {
            try {
                Optional<Category> existing = categoryService.getCategoryByTitle(c.title());

                if (existing.isPresent()) {
                    duplicateWarnings.add("Category '" + c.title() + "' already exists, skipped.");
                    continue; // idempotent
                }

                // Save and flush category
                saveCategory(c);
                categoriesImported++;

            } catch (InvalidCategoryException ex) {
                validationErrors.add("Category '" + c.title() + "': " + ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Category '" + c.title() + "': unexpected error: " + ex.getMessage());
            }
        }

        // -------------------------
        // Import Products
        // -------------------------
        for (ProductImportDTO p : bulkImportDTO.getProducts()) {
            try {
                // Resolve category
                Category category = categoryService.getCategoryByTitle(p.categoryTitle())
                        .orElseThrow(() -> new InvalidProductException(
                                "Category not found: " + p.categoryTitle()
                        ));
                
                log.info("Found category: title={}, id={}", category.getTitle(), category.getId());

                // Idempotent check
                boolean exists = productService.getProductByNameAndCategory(p.name(), category.getId()).isPresent();
                if (exists) {
                    duplicateWarnings.add("Product '" + p.name() + "' in category '" + category.getTitle() + "' already exists, skipped.");
                    continue;
                }

                // Save product safely
                saveProduct(p, category.getId());
                productsImported++;

            } catch (InvalidProductException | DatabaseOperationException ex) {
                validationErrors.add("Product '" + p.name() + "': " + ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Product '" + p.name() + "': unexpected error: " + ex.getMessage());
            }
        }

        // -------------------------
        // Build structured result
        // -------------------------
        List<String> allErrors = new ArrayList<>();
        allErrors.addAll(validationErrors);
        allErrors.addAll(databaseErrors);
        allErrors.addAll(duplicateWarnings);

        return new BulkImportResultDTO(categoriesImported, productsImported, allErrors);
    }

    // =========================
    // INTERNAL HELPERS (partial success)
    // =========================

    @Transactional
    protected void saveCategory(CategoryImportDTO c) {
        Category savedCategory = categoryService.createCategory(c.title(), c.description());

        if (savedCategory.getId() == null) {
            throw new InvalidCategoryException("Failed to persist category: " + c.title());
        }

    }

    @Transactional
    protected void saveProduct(ProductImportDTO p, Long categoryId) {
        try {
            var product = productService.createProduct(
                    p.name(),
                    p.description(),
                    categoryId
            );

            // inventoryService.createInventory(product.getId(), p.stockQuantity());
            // priceService.createPrice(product.getId(), p.currency(), BigDecimal.valueOf(p.price()));

            log.info("Product created successfully: id={}, name={}", product.getId(), product.getName());
        } catch (InvalidProductException | InvalidCategoryException | DatabaseOperationException ex) {
            // Re-throw to be caught by specific handlers above
            throw ex;
        } catch (Exception ex) {
            // Wrap only unexpected exceptions
            throw new InvalidProductException("Unexpected error creating product " + p.name() + ": " + ex.getMessage());
        }
    }
}
