package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidProductException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.Product;
import com.solveda.catalogueservice.repository.ProductRepository;
import com.solveda.catalogueservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of {@link ProductService} that manages products in the system.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Create, update, activate, and deactivate products.</li>
 *     <li>Retrieve products by ID, category, or name/category combination.</li>
 *     <li>Handles database operations with proper exception handling and logging.</li>
 * </ul>
 * </p>
 * <p>
 * All write operations are transactional. Read operations are marked
 * {@link Transactional#readOnly()} for optimized performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /* =========================
       CREATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Generates a temporary SKU and default price/stock. Validates product creation
     * and persists it to the database.
     * </p>
     *
     * @throws InvalidProductException    if validation fails
     * @throws DatabaseOperationException if database save fails
     */
    @Override
    public Product createProduct(String name, String description, Long categoryId) {
        String sku = generateSku(name);


        Product product;
        try {
            product = Product.create(
                    name,
                    description,
                    categoryId,
                    sku
            );
        } catch (IllegalArgumentException ex) {
            throw new InvalidProductException(ex.getMessage());
        }

        return saveProduct(product, "creating");
    }

    /* =========================
       UPDATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Updates product name, description, and category.
     * Throws {@link ProductNotFoundException} if the product does not exist.
     * </p>
     *
     * @throws InvalidProductException    if validation fails
     * @throws ProductNotFoundException   if product is not found
     * @throws DatabaseOperationException if database save fails
     */
    @Override
    public Product updateProduct(Long id, String newName, String newDescription, Long newCategoryId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        try {
            product.rename(newName);
            product.changeDescription(newDescription);
            product.reassignCategory(newCategoryId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidProductException(ex.getMessage());
        }

        return saveProduct(product, "updating");
    }

    /* =========================
       ACTIVATE / DEACTIVATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Activates a product and persists the change.
     * </p>
     *
     * @throws ProductNotFoundException   if product is not found
     * @throws DatabaseOperationException if database save fails
     */
    @Override
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        product.activate();
        saveProduct(product, "activating");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deactivates a product and persists the change.
     * </p>
     *
     * @throws ProductNotFoundException   if product is not found
     * @throws DatabaseOperationException if database save fails
     */
    @Override
    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        product.deactivate();
        saveProduct(product, "deactivating");
    }

    /* =========================
       READ
       ========================= */

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductByNameAndCategory(String name, Long categoryId) {
        return productRepository.findByNameAndCategoryId(name, categoryId);
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */

    /**
     * Saves a product to the database with logging and exception handling.
     *
     * @param product   product to save
     * @param operation description of the operation (creating/updating/etc.)
     * @return saved product
     * @throws DatabaseOperationException if database save fails
     */
    private Product saveProduct(Product product, String operation) {
        try {
            log.info("Saving product: name={}, categoryId={}", product.getName(), product.getCategoryId());
            return productRepository.save(product);
        } catch (DataAccessException ex) {
            log.error("Database error while {} product: name={}, error={}",
                    operation, product.getName(), ex.getMessage(), ex);
            throw new DatabaseOperationException(
                    "Error while " + operation + " product: " + product.getName(), ex);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> getAllActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    /**
     * Retrieves all products for a given category in a paginated and sortable format.
     * <p>
     * Products are filtered by {@code categoryId}. The results are returned in a
     * {@link org.springframework.data.domain.Page} to support pagination and sorting
     * according to the provided {@link Pageable} parameter.
     *
     * @param categoryId the identifier of the category
     * @param pageable   pagination and sorting information
     * @return a {@link org.springframework.data.domain.Page} containing {@link Product} entities for the category
     *
     * @throws DatabaseOperationException if there is an error accessing the database
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        try {
            return productRepository.findByCategoryId(categoryId, pageable);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error fetching products for categoryId=" + categoryId, ex
            );
        }
    }


    /**
     * Generates a SKU for the product using its name and a random UUID suffix.
     *
     * @param name product name
     * @return generated SKU
     */
    private String generateSku(String name) {
        return name.replaceAll("\\s+", "-").toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
