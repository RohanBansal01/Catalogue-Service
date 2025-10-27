package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * <pre>
 *  Repository interface for Category entity.
 *  Provides CRUD operations and custom queries for Category.
 * </pre>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by title.
     *
     * @param title the category title
     * @return category with the matching title, if exists
     */
    Category findByTitle(String title);
}
