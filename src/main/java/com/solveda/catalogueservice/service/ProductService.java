package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Product createProduct(String name, String description, Long categoryId);

    Product updateProduct(Long id, String newName, String newDescription, Long newCategoryId);

    void activateProduct(Long id);

    void deactivateProduct(Long id);

    Optional<Product> getProductById(Long id);



    List<Product> getAllActiveProducts();

    List<Product> getProductsByCategory(Long categoryId);

    /**
     * Find a product by its name and category ID.
     * Used for idempotent checks in bulk import.
     */
    Optional<Product> getProductByNameAndCategory(String name, Long categoryId);
}
