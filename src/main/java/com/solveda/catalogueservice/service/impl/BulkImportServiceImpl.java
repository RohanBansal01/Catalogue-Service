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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link BulkImportService} responsible for bulk importing
 * categories and products along with inventory and pricing data.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Validate and import categories and products from DTOs.</li>
 *     <li>Ensure idempotency by skipping duplicates.</li>
 *     <li>Handle inventory and price creation for imported products.</li>
 *     <li>Aggregate and report validation, database, and duplicate warnings.</li>
 * </ul>
 * </p>
 * <p>
 * Write operations are transactional at the method level for atomicity.
 * Separate transactional boundaries are used for saving categories, products,
 * inventory, and price to maintain consistency.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportServiceImpl implements BulkImportService {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductInventoryService inventoryService;
    private final ProductPriceService priceService;

    /**
     * {@inheritDoc}
     * <p>
     * Performs bulk import of categories and products from the provided {@link BulkImportDTO}.
     * Tracks and returns structured results including:
     * <ul>
     *     <li>Number of categories imported</li>
     *     <li>Number of products imported</li>
     *     <li>List of validation errors, database errors, and duplicate warnings</li>
     * </ul>
     * </p>
     *
     * @param bulkImportDTO DTO containing lists of categories and products to import
     * @return {@link BulkImportResultDTO} summarizing the import operation
     */
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

                // Save category
                saveCategory(c);
                categoriesImported++;

            } catch (InvalidCategoryException ex) {
                validationErrors.add("Category '" + c.title() + "': " + ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Category '" + c.title() + "': unexpected error: " + ex.getMessage());
                log.error("Error saving category '{}'", c.title(), ex);
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

                // Save product + inventory + price
                saveProduct(p, category.getId());
                productsImported++;

            } catch (InvalidProductException | DatabaseOperationException ex) {
                validationErrors.add("Product '" + p.name() + "': " + ex.getMessage());
                log.warn("Validation/database error for product '{}': {}", p.name(), ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Product '" + p.name() + "': unexpected error: " + ex.getMessage());
                log.error("Unexpected error saving product '{}'", p.name(), ex);
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
    // INTERNAL HELPERS
    // =========================

    /**
     * Saves a category using a transactional boundary.
     *
     * @param c category DTO to persist
     * @throws InvalidCategoryException if persistence fails
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveCategory(CategoryImportDTO c) {
        Category savedCategory = categoryService.createCategory(c.title(), c.description());

        if (savedCategory.getId() == null) {
            throw new InvalidCategoryException("Failed to persist category: " + c.title());
        }

        log.info("Category created successfully: id={}, title={}", savedCategory.getId(), savedCategory.getTitle());
    }

    /**
     * Saves a product, its inventory, and price using transactional boundaries.
     *
     * @param p          product DTO to persist
     * @param categoryId associated category ID
     * @throws InvalidProductException       if product creation fails
     * @throws InvalidCategoryException      if related category is invalid
     * @throws DatabaseOperationException    if any database operation fails
     */
    @Transactional(propagation = Propagation.REQUIRED)
    protected void saveProduct(ProductImportDTO p, Long categoryId) {
        try {
            // Create product
            var product = productService.createProduct(
                    p.name(),
                    p.description(),
                    categoryId
            );

            log.info("Product created successfully: id={}, name={}", product.getId(), product.getName());

            // Create inventory
            if (p.stockQuantity() != null) {
                inventoryService.createInventory(product.getId(), p.stockQuantity());
                log.info("Inventory created for product id={} with stock={}", product.getId(), p.stockQuantity());
            } else {
                log.warn("No stock quantity provided for product id={}", product.getId());
            }

            // Create price
            if (p.price() != null && p.currency() != null) {
                priceService.createPrice(
                        product.getId(),
                        p.currency(),
                        BigDecimal.valueOf(p.price())
                );
                log.info("Price created for product id={} with price={} {}", product.getId(), p.price(), p.currency());
            } else {
                log.warn("No price or currency provided for product id={}", product.getId());
            }

        } catch (InvalidProductException | InvalidCategoryException | DatabaseOperationException ex) {
            throw ex; // Propagate to caller
        } catch (Exception ex) {
            log.error("Unexpected error while saving product '{}'", p.name(), ex);
            throw new InvalidProductException("Unexpected error creating product " + p.name() + ": " + ex.getMessage());
        }
    }
}
