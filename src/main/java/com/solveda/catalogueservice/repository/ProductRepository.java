package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.model.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Provides CRUD operations and custom queries for Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all active products.
     *
     * @param active the active status
     * @return list of products matching the active status
     */
    List<Product> findByActive(Boolean active);

    /**
     * Find products by category id.
     *
     * @param categoryId the category ID
     * @return list of products in the category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find product by SKU.
     *
     * @param sku the SKU string
     * @return product with matching SKU, if exists
     */
    Product findBySku(String sku);

    /**
     * Retrieves a {@link Product} by its unique identifier along with its associated {@link Category}.
     * <p>
     * By default, the {@link Product #category} relationship is lazily loaded
     * (i.e., not fetched until accessed). Using {@link EntityGraph}
     * ensures that the category is fetched eagerly in the same query, preventing
     * potential {@code LazyInitializationException} when accessing the category outside
     * of an active Hibernate session.
     * </p>
     *
     * <p>Example usage:</p>
     * <pre>
     * Optional&lt;Product&gt; productOpt = productRepository.findById(1L);
     * Product product = productOpt.orElseThrow(() -&gt; new ProductNotFoundException("Product not found"));
     * Category category = product.getCategory(); // safely initialized
     * </pre>
     *
     * @param id the unique identifier of the product
     * @return an {@link Optional} containing the {@link Product} with its {@link Category} eagerly loaded,
     *         or {@link Optional#empty()} if no product with the given ID exists
     */
    @EntityGraph(attributePaths = "category")
    Optional<Product> findById(@NotNull Long id);

}
