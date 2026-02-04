package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing products.
 * <p>
 * Provides operations for creating, updating, activating,
 * deactivating, and retrieving products.
 * </p>
 */
public interface ProductService {

    /**
     * Creates a new product.
     *
     * @param name        product name
     * @param description product description
     * @param categoryId  category identifier
     * @return the created product
     * @throws RuntimeException if validation fails or category does not exist
     */
    Product createProduct(String name, String description, Long categoryId);

    /**
     * Updates an existing product.
     *
     * @param id             product identifier
     * @param newName        updated name
     * @param newDescription updated description
     * @param newCategoryId  updated category identifier
     * @return the updated product
     * @throws RuntimeException if product is not found or validation fails
     */
    Product updateProduct(Long id, String newName, String newDescription, Long newCategoryId);

    /**
     * Activates a product.
     *
     * @param id product identifier
     * @throws RuntimeException if product is not found
     */
    void activateProduct(Long id);

    /**
     * Deactivates a product.
     *
     * @param id product identifier
     * @throws RuntimeException if product is not found
     */
    void deactivateProduct(Long id);

    /**
     * Retrieves a product by its ID.
     *
     * @param id product identifier
     * @return an optional containing the product if found
     */
    Optional<Product> getProductById(Long id);

    /**
     * Retrieves all active products.
     *
     * @return list of active products
     */
    List<Product> getAllActiveProducts();

    /**
     * Retrieves products by category.
     *
     * @param categoryId category identifier
     * @return list of products
     */
    List<Product> getProductsByCategory(Long categoryId);

    /**
     * Finds a product by name and category.
     * <p>
     * Used for idempotent checks during bulk import.
     * </p>
     *
     * @param name       product name
     * @param categoryId category identifier
     * @return optional containing the product if found
     */
    Optional<Product> getProductByNameAndCategory(String name, Long categoryId);

    /**
     * Retrieves all active products with pagination.
     *
     * @param pageable pagination and sorting information
     * @return paginated list of active products
     */
    Page<Product> getAllActiveProducts(Pageable pageable);

    /**
     * Retrieves products by category with pagination.
     *
     * @param categoryId category identifier
     * @param pageable   pagination and sorting information
     * @return paginated list of products in the category
     */
    Page<Product> getProductsByCategory(Long categoryId, Pageable pageable);

}
