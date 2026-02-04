package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing product categories.
 * <p>
 * Provides operations for creating, updating, activating,
 * deactivating, and retrieving categories.
 * </p>
 */
public interface CategoryService {

    /**
     * Creates a new category.
     *
     * @param title       category title
     * @param description category description
     * @return the created {@link Category}
     * @throws RuntimeException if validation fails or category already exists
     */
    Category createCategory(String title, String description);

    /**
     * Updates an existing category.
     *
     * @param id            category identifier
     * @param newTitle      updated title
     * @param newDescription updated description
     * @return the updated {@link Category}
     * @throws RuntimeException if category is not found or validation fails
     */
    Category updateCategory(Long id, String newTitle, String newDescription);

    /**
     * Activates a category.
     *
     * @param id category identifier
     * @throws RuntimeException if category is not found
     */
    void activateCategory(Long id);

    /**
     * Deactivates a category.
     *
     * @param id category identifier
     * @throws RuntimeException if category is not found
     */
    void deactivateCategory(Long id);

    /**
     * Retrieves a category by its ID.
     *
     * @param id category identifier
     * @return the category
     * @throws RuntimeException if category is not found
     */
    Category getCategoryById(Long id);

    /**
     * Retrieves a category by its title.
     *
     * @param title category title
     * @return optional containing the category if found
     */
    Optional<Category> getCategoryByTitle(String title);


    /**
     * Retrieves all active categories with pagination.
     *
     * @param pageable pagination and sorting information
     * @return a page of active categories
     */
    Page<Category> getAllActiveCategories(Pageable pageable);
}
