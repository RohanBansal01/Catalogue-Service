package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product category in the catalogue system.
 * <p>
 * Each category can have multiple products associated with it
 * through a bi-directional one-to-many relationship.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categories")
public class Category {

    /**
     * Unique identifier for the category.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) /** MySQL/Postgres friendly */    private Long id;

    /**
     * Name/title of the category.
     */
    @NotBlank(message = "Category title cannot be blank")
    @Size(max = 100, message = "Category title must not exceed 100 characters")
    private String title;

    /**
     * Description providing details about the category.
     */
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    /**
     * Timestamp for when the category was created.
     * Automatically populated by Hibernate.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp for the last update of the category.
     * Automatically updated by Hibernate.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * List of products associated with this category.
     * <p>
     * This is a one-to-many relationship where one category
     * can have multiple products. Cascade operations and orphan
     * removal are enabled.
     * </p>
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Product> products = new ArrayList<>();
    /**
     * Adds a product to this category and ensures the bidirectional
     * relationship is kept consistent by also setting the category
     * reference inside the product.
     *
     * <p>Example usage:</p>
     * <pre>
     *     Category category = new Category("Electronics");
     *     Product product = new Product("Laptop");
     *     category.addProduct(product);
     * </pre>
     *
     * After calling this method:
     * <ul>
     *   <li>The product will appear in category.getProducts().</li>
     *   <li>product.getCategory() will return this category.</li>
     * </ul>
     *
     * @param product the product to be added (must not be null)
     */
    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    /**
     * Removes a product from this category and ensures the bidirectional
     * relationship is kept consistent by clearing the category reference
     * inside the product.
     *
     * <p>Example usage:</p>
     * <pre>
     *     category.removeProduct(product);
     * </pre>
     *
     * After calling this method:
     * <ul>
     *   <li>The product will no longer appear in category.getProducts().</li>
     *   <li>product.getCategory() will return null.</li>
     * </ul>
     *
     * @param product the product to be removed (must not be null)
     */
    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }


    /**
     * Equality check based only on the unique ID.
     *
     * @param o The object to compare with.
     * @return true if the IDs are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }

    /**
     * Hash code based only on the unique ID.
     *
     * @return hash code for the category.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * String representation of the category.
     * Excludes products to avoid recursion.
     *
     * @return string describing the category.
     */
    @Override
    public String toString() {
        return "Category{id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
