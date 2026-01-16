package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    /* =========================
       Identity
       ========================= */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       State
       ========================= */

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active;

    /**
     * Aggregate reference only (DDD)
     */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    /* =========================
       Auditing
       ========================= */

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* =========================
       Factory
       ========================= */

    public static Product create(
            String name,
            String description,
            Long categoryId,
            BigDecimal price,
            String sku,
            Integer stockQuantity
    ) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Product description must not be blank");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("CategoryId must be provided");
        }
        if (sku == null || sku.isBlank()) {
            throw new IllegalArgumentException("SKU must be provided");
        }

        Product product = new Product();
        product.name = name;
        product.description = description;
        product.categoryId = categoryId;
        product.price = price != null ? price : BigDecimal.ZERO;
        product.sku = sku;
        product.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        product.active = true;
        product.createdAt = LocalDateTime.now();
        product.updatedAt = product.createdAt;

        return product;
    }

    /* =========================
       Behavior
       ========================= */

    public void deactivate() {
        if (!this.active) return;
        this.active = false;
        touch();
    }

    public void activate() {
        if (this.active) return;
        this.active = true;
        touch();
    }

    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.name = newName;
        touch();
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
        touch();
    }

    public void reassignCategory(Long newCategoryId) {
        if (newCategoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        this.categoryId = newCategoryId;
        touch();
    }

    public void changeStock(Integer newStock) {
        if (newStock == null || newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stockQuantity = newStock;
        touch();
    }

    /* =========================
       Internal
       ========================= */

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
