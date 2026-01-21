package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Category} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and additional custom queries specific to categories.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     {@code
 *     Optional<Category> category = categoryRepository.findByTitle("Electronics");
 *     List<Category> activeCategories = categoryRepository.findByActiveTrue();
 *     }
 * </pre>
 * </p>
 *
 * @see JpaRepository
 * @see Category
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds a category by its unique title.
     *
     * @param title the title of the category
     * @return an {@link Optional} containing the category if found, otherwise empty
     */
    Optional<Category> findByTitle(String title);

    /**
     * Retrieves all categories that are currently active.
     *
     * @return a {@link List} of active {@link Category} entities
     */
    List<Category> findByActiveTrue();
}
