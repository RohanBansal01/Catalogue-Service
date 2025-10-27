package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.model.Product;
import com.solveda.catalogueservice.payload.ResponseStructure;
import com.solveda.catalogueservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

/**
 * REST controller for managing {@link Product} entities.
 * <p>
 * Provides endpoints to create, update, retrieve, and delete products.
 * All responses are wrapped in {@link ResponseStructure} for consistent API responses.
 * </p>
 *
 * <p>Endpoints:</p>
 * <ul>
 *     <li>POST /api/products : Create a new product</li>
 *     <li>PUT /api/products/{id} : Update an existing product</li>
 *     <li>GET /api/products/{id} : Retrieve a product by ID</li>
 *     <li>GET /api/products : Retrieve all products</li>
 *     <li>DELETE /api/products/{id} : Delete a product by ID</li>
 *     <li>GET /api/products/sku/{sku} : Retrieve a product by SKU</li>
 * </ul>
 *
 * <p>
 * Validation is applied on the request payload using {@link Valid}.
 * Exceptions from the service layer are automatically handled by
 * the global exception handler.
 * </p>
 *
 * @see Product
 * @see ProductService
 * @see ResponseStructure
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product.
     *
     * @param product the product to create (validated)
     * @return ResponseEntity containing the created product wrapped in {@link ResponseStructure}
     */
    @PostMapping
    public ResponseEntity<ResponseStructure<Product>> createProduct(@Valid @RequestBody Product product) {
        Product savedProduct = productService.createProduct(product);
        ResponseStructure<Product> response = ResponseStructure.<Product>builder()
                .status("success")
                .data(savedProduct)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing product.
     *
     * @param id      the ID of the product to update
     * @param product the updated product data (validated)
     * @return ResponseEntity containing the updated product wrapped in {@link ResponseStructure}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Product>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        ResponseStructure<Product> response = ResponseStructure.<Product>builder()
                .status("success")
                .data(updatedProduct)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a product by its unique ID.
     *
     * @param id the product ID
     * @return ResponseEntity containing the product wrapped in {@link ResponseStructure}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Product>> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        ResponseStructure<Product> response = ResponseStructure.<Product>builder()
                .status("success")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all products.
     *
     * @return ResponseEntity containing a list of all products wrapped in {@link ResponseStructure}
     */
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Product>>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        ResponseStructure<List<Product>> response = ResponseStructure.<List<Product>>builder()
                .status("success")
                .data(products)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a product by its unique ID.
     *
     * @param id the product ID to delete
     * @return ResponseEntity with {@code 204 No Content} on successful deletion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a product by its SKU.
     *
     * @param sku the SKU of the product
     * @return ResponseEntity containing the product wrapped in {@link ResponseStructure}
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ResponseStructure<Product>> getProductBySku(@PathVariable String sku) {
        Product product = productService.getProductBySku(sku);
        ResponseStructure<Product> response = ResponseStructure.<Product>builder()
                .status("success")
                .data(product)
                .build();
        return ResponseEntity.ok(response);
    }
}
