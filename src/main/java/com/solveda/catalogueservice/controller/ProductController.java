package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.ProductRequestDTO;
import com.solveda.catalogueservice.dto.ProductResponseDTO;
import com.solveda.catalogueservice.model.Product;
import com.solveda.catalogueservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing products.
 * <p>
 * Provides endpoints to create, update, activate/deactivate, and retrieve products.
 * Supports retrieving all active products and products by category.
 * </p>
 *
 * <p>All operations delegate to {@link ProductService} for business logic and persistence.</p>
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    /** Service handling product operations */
    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param request {@link ProductRequestDTO} containing product name, description, and category ID
     * @return {@link ResponseEntity} with status 201 Created and {@link ProductResponseDTO}
     */
    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductRequestDTO request) {
        Product product = productService.createProduct(
                request.name(),
                request.description(),
                request.categoryId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(product));
    }

    /**
     * Updates an existing product.
     *
     * @param id      the ID of the product to update
     * @param request {@link ProductRequestDTO} containing updated name, description, and category ID
     * @return {@link ResponseEntity} with status 200 OK and {@link ProductResponseDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {

        Product product = productService.updateProduct(
                id,
                request.name(),
                request.description(),
                request.categoryId()
        );
        return ResponseEntity.ok(toResponse(product));
    }

    /**
     * Activates a product.
     *
     * @param id the ID of the product to activate
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a product.
     *
     * @param id the ID of the product to deactivate
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product
     * @return {@link ResponseEntity} with status 200 OK and {@link ProductResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id).orElseThrow();
        return ResponseEntity.ok(toResponse(product));
    }

    /**
     * Retrieves all active products.
     *
     * @return {@link ResponseEntity} with status 200 OK and list of {@link ProductResponseDTO}
     */
    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getActive() {
        return ResponseEntity.ok(
                productService.getAllActiveProducts()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId the ID of the category
     * @return {@link ResponseEntity} with status 200 OK and list of {@link ProductResponseDTO}
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * Converts {@link Product} entity to {@link ProductResponseDTO}.
     *
     * @param product the product entity
     * @return DTO representation of the product
     */
    private ProductResponseDTO toResponse(Product product) {
        return new ProductResponseDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.isActive(),
                product.getCategoryId()
        );
    }
}
