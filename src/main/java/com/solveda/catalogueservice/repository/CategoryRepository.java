package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
     * Retrieves a paginated list of all active {@link Category} entities.
     * <p>
     * An active category is defined as having {@code active = true}.
     * The results are returned in a {@link org.springframework.data.domain.Page} to
     * support pagination and sorting.
     *
     * @param pageable the pagination and sorting information
     * @return a {@link org.springframework.data.domain.Page} containing active {@link Category} entities
     */
    Page<Category> findByActiveTrue(Pageable pageable);

}
