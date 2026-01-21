package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Product} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and additional custom queries for managing products.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     {@code
 *     List<Product> activeProducts = productRepository.findByActiveTrue();
 *     List<Product> categoryProducts = productRepository.findByCategoryId(123L);
 *     Optional<Product> product = productRepository.findByNameAndCategoryId("Laptop", 123L);
 *     }
 * </pre>
 * </p>
 *
 * @see JpaRepository
 * @see Product
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Finds all products that are currently active.
     *
     * @return a {@link List} of active {@link Product} entities
     */
    List<Product> findByActiveTrue();

    /**
     * Finds all products belonging to a specific category.
     *
     * @param categoryId the ID of the category
     * @return a {@link List} of {@link Product} entities in the category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Finds a product by its name and associated category ID.
     * <p>
     * Useful for idempotent checks when creating or importing products.
     * </p>
     *
     * @param name       the name of the product
     * @param categoryId the ID of the category
     * @return an {@link Optional} containing the {@link Product} if found, otherwise empty
     */
    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);
}
