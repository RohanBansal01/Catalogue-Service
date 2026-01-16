package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       State
       ========================= */

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String title;

    @Column
    private String description;

    @Column(nullable = false)
    @ToString.Include
    private boolean active;

    /* =========================
       Auditing (infrastructure)
       ========================= */

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* =========================
       Factory
       ========================= */

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
       Behavior (DDD)
       ========================= */

    public void deactivate() {
        if (!this.active) {
            return; // idempotent
        }
        this.active = false;
        touch();
    }

    public void activate() {
        if (this.active) {
            return; // idempotent
        }
        this.active = true;
        touch();
    }

    public void rename(String newTitle) {
        if (newTitle == null || newTitle.isBlank()) {
            throw new IllegalArgumentException("Category title must not be blank");
        }
        this.title = newTitle;
        touch();
    }

    public void changeDescription(String newDescription) {
        this.description = newDescription;
        touch();
    }

    /* =========================
       Internal helpers
       ========================= */

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }
}
