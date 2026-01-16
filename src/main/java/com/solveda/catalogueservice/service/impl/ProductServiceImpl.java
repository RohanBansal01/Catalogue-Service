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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    /* =========================
       CREATE
       ========================= */
    @Override
    public Product createProduct(String name, String description, Long categoryId) {

        // ✅ TEMP defaults (safe + production acceptable)
        BigDecimal price = BigDecimal.ZERO;
        String sku = generateSku(name);
        Integer stockQuantity = 0;

        Product product;
        try {
            product = Product.create(
                    name,
                    description,
                    categoryId,
                    price,
                    sku,
                    stockQuantity
            );
        } catch (IllegalArgumentException ex) {
            throw new InvalidProductException(ex.getMessage());
        }

        return saveProduct(product, "creating");
    }

    /* =========================
       UPDATE
       ========================= */
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
    @Override
    public void activateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));

        product.activate();
        saveProduct(product, "activating");
    }

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
    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getAllActiveProducts() {
        return productRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> getProductByNameAndCategory(String name, Long categoryId) {
        return productRepository.findByNameAndCategoryId(name, categoryId);
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */

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

    private String generateSku(String name) {
        return name.replaceAll("\\s+", "-").toUpperCase()
                + "-" + UUID.randomUUID().toString().substring(0, 6);
    }
}
