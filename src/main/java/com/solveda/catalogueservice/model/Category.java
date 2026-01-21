package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a category in the catalogue.
 * <p>
 * This entity models the core attributes of a category, including its identity,
 * state, description, and auditing information. It contains domain-specific
 * behavior for managing activation, renaming, and description changes.
 * </p>
 *
 * <p>
 * Following Domain-Driven Design (DDD) principles, this class acts as an aggregate root
 * for all products belonging to this category.
 * </p>
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Category {

    /* =========================
       Identity
       ========================= */

    /** Primary key of the category. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       State
       ========================= */

    /** The unique title of the category. Cannot be blank. */
    @Column(nullable = false, unique = true)
    @ToString.Include
    private String title;

    /** Optional description of the category. */
    @Column
    private String description;

    /** Whether the category is active and visible in the catalogue. */
    @Column(nullable = false)
    @ToString.Include
    private boolean active;

    /* =========================
       Auditing
       ========================= */

    /** Timestamp of creation. Immutable after creation. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of last update. Updated automatically on changes. */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* =========================
       Factory Method
       ========================= */

    /**
     * Factory method to create a new {@link Category}.
     *
     * @param title       category title, must not be blank
     * @param description optional category description
     * @return a fully initialized {@link Category} instance
     * @throws IllegalArgumentException if title is null or blank
     */
    public static Category create(String title, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Category title must not be blank");
        }

        Category category = new Category();
        category.title = title;
        category.description = description;
        category.active = true;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = category.createdAt;

        return category;
    }

    /* =========================
       Domain Behavior
       ========================= */

    /**
     * Deactivates the category (sets {@code active} to false).
     * Idempotent operation.
     */
    public void deactivate() {
        if (!this.active) return;
        this.active = false;
        touch();
    }

    /**
     * Activates the category (sets {@code active} to true).
     * Idempotent operation.
     */
    public void activate() {
        if (this.active) return;
        this.active = true;
        touch();
    }

    /**
     * Renames the category.
     *
     * @param newTitle new category title, must not be blank
     * @throws IllegalArgumentException if newTitle is null or blank
     */
    public void rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Category title must not be blank");
        }
        this.title = newTitle;
        touch();
    }

    /**
     * Updates the category description.
     *
     * @param newDescription new description text (nullable)
     */
    public void changeDescription(String newDescription) {
        this.description = newDescription;
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
