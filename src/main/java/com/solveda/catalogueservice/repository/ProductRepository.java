package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Product} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and custom queries for retrieving products based on status, category, and name.
 * Supports both paginated and non-paginated queries.
 * </p>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // =========================
    // Non-paginated queries
    // =========================

    /**
     * Retrieves all products that are currently active.
     *
     * @return list of active {@link Product} entities
     */
    List<Product> findByActiveTrue();

    /**
     * Retrieves all products belonging to a specific category.
     *
     * @param categoryId the identifier of the category
     * @return list of {@link Product} entities for the given category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Finds a product by its name and category.
     * <p>
     * Useful for idempotent checks, e.g., during bulk imports.
     * </p>
     *
     * @param name       the product name
     * @param categoryId the category identifier
     * @return an {@link Optional} containing the {@link Product} if found, otherwise empty
     */
    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);

    // =========================
    // Paginated queries
    // =========================

    /**
     * Retrieves active products in a paginated format.
     *
     * @param pageable pagination and sorting information
     * @return a {@link Page} of active {@link Product} entities
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Retrieves products for a specific category in a paginated format.
     *
     * @param categoryId the identifier of the category
     * @param pageable   pagination and sorting information
     * @return a {@link Page} of {@link Product} entities for the category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}
