package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a product in the catalogue.
 * <p>
 * This entity models the core attributes of a product, including its identity, state,
 * inventory, pricing reference, and auditing information. It also contains domain-specific
 * behavior for managing activation, renaming, stock updates, and category reassignments.
 * </p>
 *
 * <p>
 * Following Domain-Driven Design (DDD) principles, this class acts as an aggregate root.
 * </p>
 */
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

    /** Primary key for the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       State
       ========================= */

    /** The name of the product. Cannot be blank. */
    @Column(nullable = false)
    private String name;

    /** The detailed description of the product. Cannot be blank. */
    @Column(nullable = false)
    private String description;

    /** Whether the product is active (available for sale). */
    @Column(nullable = false)
    private boolean active;

    /** Reference to the associated category (DDD aggregate reference). */
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    /** Default price of the product. */
    @Column(nullable = false)
    private BigDecimal price;

    /** Unique SKU (Stock Keeping Unit) identifier. */
    @Column(nullable = false, unique = true)
    private String sku;

    /** Current stock quantity for the product. */
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    /* =========================
       Auditing
       ========================= */

    /** Timestamp of product creation. Immutable after creation. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of the last update to the product. */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /* =========================
       Factory Method
       ========================= */

    /**
     * Factory method to create a new {@link Product} instance.
     *
     * @param name          product name, must not be blank
     * @param description   product description, must not be blank
     * @param categoryId    ID of the associated category, must not be null
     * @param price         initial price (if null, defaults to 0)
     * @param sku           unique SKU, must not be blank
     * @param stockQuantity initial stock quantity (if null, defaults to 0)
     * @return a fully initialized {@link Product} instance
     * @throws IllegalArgumentException if name, description, categoryId, or SKU is invalid
     */
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
       Domain Behavior
       ========================= */

    /**
     * Deactivates the product (sets {@code active} to false).
     * Safe to call multiple times.
     */
    public void deactivate() {
        if (!this.active) return;
        this.active = false;
        touch();
    }

    /**
     * Activates the product (sets {@code active} to true).
     * Safe to call multiple times.
     */
    public void activate() {
        if (this.active) return;
        this.active = true;
        touch();
    }

    /**
     * Renames the product.
     *
     * @param newName new product name, must not be blank
     * @throws IllegalArgumentException if newName is null or blank
     */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.name = newName;
        touch();
    }

    /**
     * Changes the product description.
     *
     * @param newDescription new product description
     */
    public void changeDescription(String newDescription) {
        this.description = newDescription;
        touch();
    }

    /**
     * Reassigns the product to a new category.
     *
     * @param newCategoryId new category ID, must not be null
     * @throws IllegalArgumentException if newCategoryId is null
     */
    public void reassignCategory(Long newCategoryId) {
        if (newCategoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        this.categoryId = newCategoryId;
        touch();
    }

    /**
     * Updates the product stock quantity.
     *
     * @param newStock new stock quantity, must be non-negative
     * @throws IllegalArgumentException if newStock is null or negative
     */
    public void changeStock(Integer newStock) {
        if (newStock == null || newStock < 0) {
            throw new IllegalArgumentException("Stock cannot be negative");
        }
        this.stockQuantity = newStock;
        touch();
    }

    /* =========================
       Internal Helpers
       ========================= */

    /** Updates the {@code updatedAt} timestamp to the current time. */
    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
