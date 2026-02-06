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
 * Service implementation responsible for bulk importing catalogue data (categories and products)
 * in a controlled batched manner.
 *
 * <p><b>Responsibilities</b></p>
 * <ul>
 *   <li>Validates input request and batch size parameters.</li>
 *   <li>Processes categories and products in configurable batch sizes to reduce DB load.</li>
 *   <li>Handles duplicate detection for categories and products.</li>
 *   <li>Performs product dependent operations such as inventory and price creation.</li>
 *   <li>Collects validation errors, database errors, and duplicate warnings into a unified response.</li>
 * </ul>
 *
 * <p><b>Transactional Strategy</b></p>
 * <ul>
 *   <li>Main {@link #importData(BulkImportDTO, int, int)} method is intentionally NOT transactional.</li>
 *   <li>Each batch is executed inside its own independent transaction using
 *       {@link Propagation#REQUIRES_NEW}.</li>
 *   <li>This ensures one batch failure does not rollback previously successful batches.</li>
 * </ul>
 *
 * <p><b>Error Handling Strategy</b></p>
 * <ul>
 *   <li>Validation/domain errors are collected into {@code validationErrors}.</li>
 *   <li>Unexpected runtime/DB errors are collected into {@code databaseErrors}.</li>
 *   <li>Duplicate skips are collected into {@code duplicateWarnings}.</li>
 * </ul>
 *
 * <p><b>Performance Notes</b></p>
 * <ul>
 *   <li>Batch processing reduces transaction size and avoids long-running locks.</li>
 *   <li>Each record still triggers service calls; DB optimization depends on internal service implementations.</li>
 *   <li>Partitioning uses {@link List#subList(int, int)} which is O(1) view-based slicing.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BulkImportServiceImpl implements BulkImportService {

    /**
     * Default category batch size used when caller provides invalid size.
     */
    private static final int DEFAULT_CATEGORY_BATCH_SIZE = 10;

    /**
     * Default product batch size used when caller provides invalid size.
     */
    private static final int DEFAULT_PRODUCT_BATCH_SIZE = 100;

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductInventoryService inventoryService;
    private final ProductPriceService priceService;

    /**
     * Imports categories and products from the provided {@link BulkImportDTO}.
     *
     * <p>Categories are imported first because products depend on category existence.</p>
     *
     * <p>Both categories and products are processed in batches. Each batch is executed
     * in a separate transaction to ensure partial progress is preserved even if a batch fails.</p>
     *
     * @param bulkImportDTO      request payload containing categories and products.
     * @param categoryBatchSize  number of categories processed per batch; defaults if invalid.
     * @param productBatchSize   number of products processed per batch; defaults if invalid.
     * @return import result containing import counts and consolidated error/warning messages.
     * @throws IllegalArgumentException if {@code bulkImportDTO} is null.
     */
    @Override
    public BulkImportResultDTO importData(BulkImportDTO bulkImportDTO, int categoryBatchSize, int productBatchSize) {

        if (bulkImportDTO == null) {
            throw new IllegalArgumentException("BulkImportDTO must not be null");
        }

        if (categoryBatchSize <= 0) {
            categoryBatchSize = DEFAULT_CATEGORY_BATCH_SIZE;
        }

        if (productBatchSize <= 0) {
            productBatchSize = DEFAULT_PRODUCT_BATCH_SIZE;
        }

        final List<String> validationErrors = new ArrayList<>();
        final List<String> databaseErrors = new ArrayList<>();
        final List<String> duplicateWarnings = new ArrayList<>();

        int categoriesImported = 0;
        int productsImported = 0;

        // =========================
        // CATEGORY BATCH PROCESSING
        // =========================
        List<CategoryImportDTO> categoryList = bulkImportDTO.getCategories();

        if (categoryList != null && !categoryList.isEmpty()) {

            List<List<CategoryImportDTO>> categoryBatches = partitionList(categoryList, categoryBatchSize);

            for (int i = 0; i < categoryBatches.size(); i++) {

                List<CategoryImportDTO> batch = categoryBatches.get(i);
                log.info("Processing category batch {}/{} (size={})",
                        i + 1, categoryBatches.size(), batch.size());

                try {
                    categoriesImported += saveCategoryBatch(batch, validationErrors, databaseErrors, duplicateWarnings);
                } catch (Exception ex) {
                    databaseErrors.add("Category batch failed (batch=" + (i + 1) + "): " + ex.getMessage());
                    log.error("Category batch {} failed", (i + 1), ex);
                }
            }
        }

        // =========================
        // PRODUCT BATCH PROCESSING
        // =========================
        List<ProductImportDTO> productList = bulkImportDTO.getProducts();

        if (productList != null && !productList.isEmpty()) {

            List<List<ProductImportDTO>> productBatches = partitionList(productList, productBatchSize);

            for (int i = 0; i < productBatches.size(); i++) {

                List<ProductImportDTO> batch = productBatches.get(i);
                log.info("Processing product batch {}/{} (size={})",
                        i + 1, productBatches.size(), batch.size());

                try {
                    productsImported += saveProductBatch(batch, validationErrors, databaseErrors, duplicateWarnings);
                } catch (Exception ex) {
                    databaseErrors.add("Product batch failed (batch=" + (i + 1) + "): " + ex.getMessage());
                    log.error("Product batch {} failed", (i + 1), ex);
                }
            }
        }

        // =========================
        // FINAL RESPONSE
        // =========================
        List<String> allErrors = new ArrayList<>();
        allErrors.addAll(validationErrors);
        allErrors.addAll(databaseErrors);
        allErrors.addAll(duplicateWarnings);

        return new BulkImportResultDTO(categoriesImported, productsImported, allErrors);
    }

    /**
     * Persists a batch of categories.
     *
     * <p>Transaction is isolated using {@link Propagation#REQUIRES_NEW}, ensuring that
     * failures inside this batch do not rollback other batches.</p>
     *
     * <p>Duplicate detection is performed using category title lookup.</p>
     *
     * @param batch              list of categories to persist.
     * @param validationErrors   shared list where validation errors are appended.
     * @param databaseErrors     shared list where unexpected DB errors are appended.
     * @param duplicateWarnings  shared list where duplicate skip messages are appended.
     * @return number of categories successfully imported in this batch.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int saveCategoryBatch(
            List<CategoryImportDTO> batch,
            List<String> validationErrors,
            List<String> databaseErrors,
            List<String> duplicateWarnings
    ) {
        int importedCount = 0;

        for (CategoryImportDTO c : batch) {
            try {
                Optional<Category> existing = categoryService.getCategoryByTitle(c.title());

                if (existing.isPresent()) {
                    duplicateWarnings.add("Category '" + c.title() + "' already exists, skipped.");
                    continue;
                }

                Category savedCategory = categoryService.createCategory(c.title(), c.description());

                if (savedCategory.getId() == null) {
                    throw new InvalidCategoryException("Failed to persist category: " + c.title());
                }

                importedCount++;
                log.info("Category created successfully: id={}, title={}", savedCategory.getId(), savedCategory.getTitle());

            } catch (InvalidCategoryException ex) {
                validationErrors.add("Category '" + c.title() + "': " + ex.getMessage());
                log.warn("Validation error for category '{}': {}", c.title(), ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Category '" + c.title() + "': unexpected error: " + ex.getMessage());
                log.error("Unexpected error saving category '{}'", c.title(), ex);
            }
        }

        return importedCount;
    }

    /**
     * Persists a batch of products.
     *
     * <p>Transaction is isolated using {@link Propagation#REQUIRES_NEW} so that failures
     * in product processing do not rollback other successful batches.</p>
     *
     * <p>Validation rules applied:</p>
     * <ul>
     *   <li>Product category must exist.</li>
     *   <li>Product name must be unique within a category.</li>
     *   <li>If stock is provided, inventory record is created.</li>
     *   <li>If price and currency are provided, price record is created.</li>
     * </ul>
     *
     * <p>Dependent entity creation:</p>
     * <ul>
     *   <li>Inventory and price are created only after product is successfully persisted.</li>
     *   <li>If inventory/price creation fails, the error is recorded as validation/database failure.</li>
     * </ul>
     *
     * @param batch              list of products to persist.
     * @param validationErrors   shared list where validation errors are appended.
     * @param databaseErrors     shared list where unexpected DB errors are appended.
     * @param duplicateWarnings  shared list where duplicate skip messages are appended.
     * @return number of products successfully imported in this batch.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected int saveProductBatch(
            List<ProductImportDTO> batch,
            List<String> validationErrors,
            List<String> databaseErrors,
            List<String> duplicateWarnings
    ) {
        int importedCount = 0;

        for (ProductImportDTO p : batch) {
            try {
                Category category = categoryService.getCategoryByTitle(p.categoryTitle())
                        .orElseThrow(() -> new InvalidProductException("Category not found: " + p.categoryTitle()));

                boolean exists = productService.getProductByNameAndCategory(p.name(), category.getId()).isPresent();

                if (exists) {
                    duplicateWarnings.add("Product '" + p.name() + "' in category '" + category.getTitle() + "' already exists, skipped.");
                    continue;
                }

                var product = productService.createProduct(
                        p.name(),
                        p.description(),
                        category.getId()
                );

                log.info("Product created successfully: id={}, name={}", product.getId(), product.getName());

                if (p.stockQuantity() != null) {
                    inventoryService.createInventory(product.getId(), p.stockQuantity());
                    log.info("Inventory created for product id={} with stock={}", product.getId(), p.stockQuantity());
                }

                if (p.price() != null && p.currency() != null) {
                    priceService.createPrice(
                            product.getId(),
                            p.currency(),
                            BigDecimal.valueOf(p.price())
                    );
                    log.info("Price created for product id={} with price={} {}", product.getId(), p.price(), p.currency());
                }

                importedCount++;

            } catch (InvalidProductException | DatabaseOperationException ex) {
                validationErrors.add("Product '" + p.name() + "': " + ex.getMessage());
                log.warn("Validation/database error for product '{}': {}", p.name(), ex.getMessage());
            } catch (Exception ex) {
                databaseErrors.add("Product '" + p.name() + "': unexpected error: " + ex.getMessage());
                log.error("Unexpected error saving product '{}'", p.name(), ex);
            }
        }

        return importedCount;
    }

    /**
     * Partitions a list into smaller sublists of the specified batch size.
     *
     * <p>This method returns a list of views backed by the original list using {@link List#subList(int, int)}.
     * The returned sublists should not be structurally modified.</p>
     *
     * @param list      source list to partition.
     * @param batchSize max size of each partition.
     * @param <T>       list element type.
     * @return list of partitions, each containing up to {@code batchSize} elements.
     * @throws IllegalArgumentException if {@code batchSize <= 0}.
     */
    private <T> List<List<T>> partitionList(List<T> list, int batchSize) {

        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0");
        }

        List<List<T>> batches = new ArrayList<>();

        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(list.subList(i, end));
        }

        return batches;
    }
}