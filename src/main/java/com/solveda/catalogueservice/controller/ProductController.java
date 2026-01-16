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

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(
            @Valid @RequestBody ProductRequestDTO request) {

        Product product =
                productService.createProduct(
                        request.name(),
                        request.description(),
                        request.categoryId()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO request) {

        Product product =
                productService.updateProduct(
                        id,
                        request.name(),
                        request.description(),
                        request.categoryId()
                );

        return ResponseEntity.ok(toResponse(product));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        productService.activateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        productService.deactivateProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getById(@PathVariable Long id) {
        Product product = productService.getProductById(id).orElseThrow();
        return ResponseEntity.ok(toResponse(product));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponseDTO>> getActive() {
        return ResponseEntity.ok(
                productService.getAllActiveProducts()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(
            @PathVariable Long categoryId) {

        return ResponseEntity.ok(
                productService.getProductsByCategory(categoryId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

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
