package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a product in the catalogue.
 * <p>
 * This entity models the core attributes of a product, including its identity, state,
 * category reference, and auditing information. Price and stock are now tracked
 * separately via {@link ProductPrice} and inventory management.
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

    /** Unique SKU (Stock Keeping Unit) identifier. */
    @Column(nullable = false, unique = true)
    private String sku;

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
     * @param name       product name, must not be blank
     * @param description product description, must not be blank
     * @param categoryId ID of the associated category, must not be null
     * @param sku        unique SKU, must not be blank
     * @return a fully initialized {@link Product} instance
     * @throws IllegalArgumentException if name, description, categoryId, or SKU is invalid
     */
    public static Product create(
            String name,
            String description,
            Long categoryId,
            String sku
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
        product.sku = sku;
        product.active = true;
        product.createdAt = LocalDateTime.now();
        product.updatedAt = product.createdAt;

        return product;
    }

    /* =========================
       Domain Behavior
       ========================= */

    /** Deactivates the product. Safe to call multiple times. */
    public void deactivate() {
        if (!this.active) return;
        this.active = false;
        touch();
    }

    /** Activates the product. Safe to call multiple times. */
    public void activate() {
        if (this.active) return;
        this.active = true;
        touch();
    }

    /** Renames the product. */
    public void rename(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        this.name = newName;
        touch();
    }

    /** Changes the product description. */
    public void changeDescription(String newDescription) {
        this.description = newDescription;
        touch();
    }

    /** Reassigns the product to a new category. */
    public void reassignCategory(Long newCategoryId) {
        if (newCategoryId == null) {
            throw new IllegalArgumentException("CategoryId must not be null");
        }
        this.categoryId = newCategoryId;
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
