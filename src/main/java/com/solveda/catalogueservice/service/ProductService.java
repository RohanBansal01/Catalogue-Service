package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.Product;

import java.util.List;

/**
 * Service interface for managing products in the catalogue system.
 */
public interface ProductService {
    /**
     * Create a new product.
     *
     * @param product the product to be created
     * @return the saved product
     */
    @SuppressWarnings("unused")
    Product createProduct(Product product);

    /**
     * Update an existing product.
     * @param product the product with updated information
     * @return the updated product
     */
    @SuppressWarnings("unused")
    Product updateProduct(Product product);

    /**
     * Retrieve a product by its ID.
     *
     * @param id the product ID
     * @return an Optional containing the product if found, or empty if not
     */
    @SuppressWarnings("unused")
    Product getProductById(Long id);

    /**
     * Return all products.
     *
     * @return list of all products
     */
    @SuppressWarnings("unused")
    List<Product> getAllProducts();

    /**
     * Delete a product by its ID.
     *
     * @param id the product ID
     */
    @SuppressWarnings("unused")
    void deleteProduct(Long id);

    /**
     * Retrieve all active products.
     *
     * @return list of active products
     */
    @SuppressWarnings("unused")
    List<Product> getActiveProducts();

    /**
     * Retrieve all products in a specific category.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    @SuppressWarnings("unused")
    List<Product> getProductsByCategory(Long categoryId);

    /**
     * Find a product by its SKU.
     *
     * @param sku the product SKU
     * @return the product if found, null otherwise
     */
    @SuppressWarnings("unused")
    Product getProductBySku(String sku);
}
