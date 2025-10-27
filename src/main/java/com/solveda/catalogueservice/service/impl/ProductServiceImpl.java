package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidProductException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.Product;
import com.solveda.catalogueservice.repository.CategoryRepository;
import com.solveda.catalogueservice.repository.ProductRepository;
import com.solveda.catalogueservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link ProductService} interface.
 * <p>
 * This class encapsulates the business logic and CRUD operations for managing {@link Product} entities.
 * It leverages {@link ProductRepository} for persistence and ensures robust error handling using
 * custom exceptions for validation, data access, and business rule violations.
 * </p>
 * <p>
 * Java 8 {@link Optional} and lambda expressions are used for concise, null-safe operations
 * without introducing performance overhead.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    /**
     * Repository responsible for performing persistence operations on {@link Product} entities.
     * <p>
     * Automatically injected via constructor by Lombok’s {@code @RequiredArgsConstructor}.
     * </p>
     */
    private final ProductRepository productRepository;

    /**
     * Repository responsible for performing persistence operations on {@link com.solveda.catalogueservice.model.Category } entities.
     */
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new {@link Product} record in the catalogue.
     *
     * @param product the {@link Product} entity to be persisted
     * @return the saved {@link Product} entity
     * @throws InvalidProductException     if product data is invalid or incomplete
     * @throws DatabaseOperationException  if a database access error occurs during save
     */
    @Override
    public Product createProduct(Product product) {
        validateProduct(product);
        try {
            return productRepository.save(product);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while saving product: " + product.getName(), ex);
        }
    }

    /**
     * Updates an existing {@link Product}.
     * <p>
     * Uses {@link Optional} to handle cases where the product does not exist.
     * </p>
     *
     * @param product the {@link Product} entity containing updated information
     * @return the updated {@link Product} entity
     * @throws ProductNotFoundException    if the product does not exist in the database
     * @throws InvalidProductException     if the provided product data is invalid
     * @throws DatabaseOperationException  if a database access error occurs during update
     */
    @Override
    public Product updateProduct(Product product) {
        validateProduct(product);
        return Optional.ofNullable(product.getId())
                .flatMap(productRepository::findById)
                .map(existing -> {
                    existing.setName(product.getName());
                    existing.setPrice(product.getPrice());
                    existing.setActive(product.getActive());
                    existing.assignCategory(
                            Optional.ofNullable(product.getCategory())
                                    .map(c -> categoryRepository.findById(c.getId())
                                            .orElseThrow(() -> new InvalidProductException(
                                                    "Category with ID " + c.getId() + " not found")))
                                    .orElse(null) // if product.getCategory() is null, assign null
                    );
                    existing.setSku(product.getSku());
                    try {
                        return productRepository.save(existing);
                    } catch (DataAccessException ex) {
                        throw new DatabaseOperationException("Error while updating product ID: " + product.getId(), ex);
                    }
                })
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + product.getId() + " not found"));
    }

    /**
     * Retrieves a {@link Product} by its unique identifier.
     *
     * @param id the product ID
     * @return the matching {@link Product} entity
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
    }

    /**
     * Retrieves a {@link Product} by its SKU (Stock Keeping Unit).
     *
     * @param sku the SKU of the product
     * @return the matching {@link Product} entity
     * @throws ProductNotFoundException if no product with the given SKU exists
     */
    @Override
    public Product getProductBySku(String sku) {
        return Optional.ofNullable(productRepository.findBySku(sku))
                .orElseThrow(() -> new ProductNotFoundException("Product with SKU '" + sku + "' not found"));
    }

    /**
     * Retrieves all {@link Product} entities from the catalogue.
     *
     * @return a list of all {@link Product} entities
     * @throws DatabaseOperationException if a database error occurs during retrieval
     */
    @Override
    public List<Product> getAllProducts() {
        try {
            return productRepository.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while fetching all products", ex);
        }
    }

    /**
     * Retrieves all active {@link Product} entities.
     * <p>
     * Delegates filtering to the database for optimal performance.
     * </p>
     *
     * @return a list of active {@link Product} entities
     * @throws DatabaseOperationException if a database error occurs during retrieval
     */
    @Override
    public List<Product> getActiveProducts() {
        try {
            return productRepository.findByActive(true);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while fetching active products", ex);
        }
    }

    /**
     * Retrieves all {@link Product} entities belonging to a specific category.
     *
     * @param categoryId the category ID
     * @return a list of {@link Product} entities under the specified category
     * @throws DatabaseOperationException if a database error occurs during retrieval
     */
    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        try {
            return productRepository.findByCategoryId(categoryId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while fetching products for category ID: " + categoryId, ex);
        }
    }

    /**
     * Deletes a {@link Product} by its unique identifier.
     *
     * @param id the product ID to delete
     * @throws ProductNotFoundException   if no product exists with the given ID
     * @throws DatabaseOperationException if a database error occurs during deletion
     */
    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product with ID " + id + " not found for deletion");
        }
        try {
            productRepository.deleteById(id);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while deleting product ID: " + id, ex);
        }
    }

/**
     * Validates the integrity and completeness of a {@link Product} entity
     * before performing persistence or update operations.
     * <p>
     * This method ensures that all required product attributes conform
     * to business and database constraints, preventing invalid data
     * from being stored in the system.
     * </p>
     *
     * <p><b>Validation Rules:</b></p>
     * <ul>
     *   <li>{@code product} must not be {@code null}</li>
     *   <li>{@code name} and {@code description} must not be blank</li>
     *   <li>{@code price} must be non-null and non-negative</li>
     *   <li>{@code sku} must be non-null and non-blank</li>
     *   <li>{@code stockQuantity} must be non-null and non-negative</li>
     *   <li>{@code active} status must not be {@code null}</li>
     * </ul>
     *
     * @param product the {@link Product} entity to validate
     * @throws InvalidProductException if any product attribute is invalid or missing
     */
    private void validateProduct(Product product) {
        if (product == null) {
            throw new InvalidProductException("Product cannot be null");
        }

        if (product.getName() == null || product.getName().isBlank()) {
            throw new InvalidProductException("Product name cannot be blank");
        }

        if (product.getDescription() == null || product.getDescription().isBlank()) {
            throw new InvalidProductException("Product description cannot be blank");
        }

        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new InvalidProductException("Product price must be non-negative");
        }

        if (product.getSku() == null || product.getSku().isBlank()) {
            throw new InvalidProductException("Product SKU cannot be blank");
        }

        if (product.getStockQuantity() == null || product.getStockQuantity() < 0) {
            throw new InvalidProductException("Stock quantity must be non-negative");
        }

        if (product.getActive() == null) {
            throw new InvalidProductException("Product active status cannot be null");
        }
    }

}
