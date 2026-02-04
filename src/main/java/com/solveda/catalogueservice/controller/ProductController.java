package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.dto.ProductRequestDTO;
import com.solveda.catalogueservice.dto.ProductResponseDTO;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.Product;
import com.solveda.catalogueservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing {@link Product} entities.
 * <p>
 * Provides endpoints to create, update, activate, deactivate, and retrieve products.
 * Supports pagination, sorting, and filtering by category.
 * All responses are wrapped in appropriate HTTP status codes and DTOs.
 * </p>
 */
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Creates a new product.
     *
     * @param request {@link ProductRequestDTO} containing product details
     * @return {@link ResponseEntity} containing {@link ProductResponseDTO} with status 201 CREATED
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
     * @param request {@link ProductRequestDTO} containing updated product details
     * @return {@link ResponseEntity} containing {@link ProductResponseDTO} with status 200 OK
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
     * @return {@link ResponseEntity} with status 204 NO CONTENT
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
     * @return {@link ResponseEntity} with status 204 NO CONTENT
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
     * @return {@link ResponseEntity} containing {@link ProductResponseDTO} with status 200 OK
     * @throws ProductNotFoundException if the product does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found"));
        return ResponseEntity.ok(toResponse(product));
    }

    /**
     * Retrieves all active products with pagination and sorting.
     *
     * @param page      zero-based page index (default 0)
     * @param size      number of items per page (default 10)
     * @param sort      field to sort by (default "id")
     * @param direction sort direction, ASC or DESC (default ASC)
     * @return {@link ResponseEntity} containing {@link PaginatedResponseDTO} of active products
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<ProductResponseDTO>> getActive(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortDirection(direction), sort));
        Page<Product> productPage = productService.getAllActiveProducts(pageable);
        return buildPaginatedResponse(productPage);
    }

    /**
     * Retrieves products for a specific category with pagination and sorting.
     *
     * @param categoryId the ID of the category
     * @param page       zero-based page index (default 0)
     * @param size       number of items per page (default 10)
     * @param sort       field to sort by (default "id")
     * @param direction  sort direction, ASC or DESC (default ASC)
     * @return {@link ResponseEntity} containing {@link PaginatedResponseDTO} of products for the category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PaginatedResponseDTO<ProductResponseDTO>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSortDirection(direction), sort));
        Page<Product> productPage = productService.getProductsByCategory(categoryId, pageable);
        return buildPaginatedResponse(productPage);
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Converts a {@link Product} entity into a {@link ProductResponseDTO}.
     *
     * @param product the product entity
     * @return the corresponding {@link ProductResponseDTO}
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

    /**
     * Parses a string into a {@link Sort.Direction}.
     *
     * @param direction the direction string (ASC or DESC)
     * @return the corresponding {@link Sort.Direction}
     * @throws IllegalArgumentException if the direction is invalid
     */
    private Sort.Direction parseSortDirection(String direction) {
        try {
            return Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Invalid sort direction: " + direction);
        }
    }

    /**
     * Builds a paginated response from a {@link Page} of {@link Product} entities.
     *
     * @param page the page of product entities
     * @return {@link ResponseEntity} containing {@link PaginatedResponseDTO} with product DTOs
     *         and pagination metadata
     */
    private ResponseEntity<PaginatedResponseDTO<ProductResponseDTO>> buildPaginatedResponse(Page<Product> page) {
        return ResponseEntity.ok(
                new PaginatedResponseDTO<>(
                        page.getContent().stream().map(this::toResponse).toList(),
                        page.getNumber(),
                        page.getSize(),
                        page.getTotalElements(),
                        page.getTotalPages(),
                        page.isFirst(),
                        page.isLast()
                )
        );
    }
}
