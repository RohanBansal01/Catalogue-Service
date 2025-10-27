package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Represents a product in the catalogue system.
 * <p>
 * Each product belongs to a single category. This entity
 * includes basic product information like name, price,
 * stock quantity, and SKU.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products") // match DB table name
public class Product {

    /**
     * Unique identifier for the product.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product.
     */
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    /**
     * Description providing details about the product.
     */
    @NotBlank(message = "Description cannot be blank")
    private String description;

    /**
     * Price of the product.
     */
    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must not be negative")
    private Double price;

    /**
     * Available stock quantity for the product.
     */
    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    /**
     * Stock Keeping Unit (SKU) for the product.
     *
     * <p>
     * The SKU is a unique identifier used to track and manage products in the catalogue.
     * It must be non-blank and unique across all products in the database.
     * </p>
     *
     * <p><b>Constraints:</b></p>
     * <ul>
     *     <li>Database: {@code UNIQUE} constraint ensures no two products share the same SKU.</li>
     *     <li>Validation: {@link  NotBlank} ensures the SKU cannot be null, empty, or only whitespace.</li>
     * </ul>
     *
     * <p>Example usage:</p>
     * <pre>
     *     Product product = new Product();
     *     product.setSku("ABC-12345");
     * </pre>
     *
     * <p>Notes:</p>
     * <ul>
     *     <li>Changing an SKU for an existing product should be done carefully, as it may affect references in other systems or logs.</li>
     *     <li>SKU should follow a consistent format for better product management and searchability.</li>
     * </ul>
     */
    @Column(unique = true)
    @NotBlank(message = "SKU cannot be blank")
    private String sku;


    /**
     * Flag indicating whether the product is active (available for sale).
     */
    @NotNull(message = "Active status must be specified")
    private Boolean active;

    /**
     * Timestamp for when the product was created.
     * Automatically populated by Hibernate.
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Timestamp for the last update of the product.
     * Automatically updated by Hibernate.
     */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Category to which this product belongs.
     * <p>
     * Many products can belong to one category (ManyToOne relationship).
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    /**
     * Assigns this product to the given category and ensures the
     * bidirectional relationship is kept consistent by also adding
     * this product to the category's product list.
     *
     * <p>Example usage:</p>
     * <pre>
     *     Product product = new Product("Laptop");
     *     Category category = new Category("Electronics");
     *     product.assignCategory(category);
     * </pre>
     *
     * After calling this method:
     * <ul>
     *   <li>product.getCategory() will return the given category.</li>
     *   <li>The product will appear in category.getProducts().</li>
     * </ul>
     *
     * @param category the category to assign this product to (may be null)
     */
    public void assignCategory(Category category) {
        this.category = category;
        if (category != null && !category.getProducts().contains(this)) {
            category.getProducts().add(this);
        }
    }

    /**
     * Removes this product from its current category and ensures
     * the bidirectional relationship is kept consistent by also
     * removing this product from the category's product list.
     *
     * <p>Example usage:</p>
     * <pre>
     *     product.removeCategory();
     * </pre>
     *
     * After calling this method:
     * <ul>
     *   <li>product.getCategory() will return null.</li>
     *   <li>The product will no longer appear in its old category.getProducts().</li>
     * </ul>
     */
    public void removeCategory() {
        if (this.category != null) {
            this.category.getProducts().remove(this);
            this.category = null;
        }
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
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return id != null && id.equals(product.id);
    }

    /**
     * Hash code based only on the unique ID.
     *
     * @return hash code for the product.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /**
     * String representation of the product.
     * Excludes category to avoid recursion.
     *
     * @return string describing the product.
     */
    @Override
    public String toString() {
        return "Product{id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", active=" + active +
                '}';
    }
}
