package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.exception.CategoryNotFoundException;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidCategoryException;
import com.solveda.catalogueservice.model.Category;

import java.util.List;

/**
 * Service interface defining operations for managing {@link Category} entities.
 * <p>
 * Acts as an abstraction layer between controllers and the underlying
 * repository implementation. This ensures loose coupling and testability.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Define CRUD operations for {@link Category}.</li>
 *     <li>Expose domain-specific finder methods like {@link #getCategoryByTitle(String)}.</li>
 *     <li>Enforce validation and exception handling contracts implemented by the service layer.</li>
 * </ul>
 *
 * @see com.solveda.catalogueservice.service.impl.CategoryServiceImpl
 * @see com.solveda.catalogueservice.model.Category
 * @see com.solveda.catalogueservice.repository.CategoryRepository
 */
public interface CategoryService {

    /**
     * Creates a new {@link Category} after validation.
     *
     * @param category the category entity to be created
     * @return the persisted {@link Category}
     * @throws InvalidCategoryException if the category data is invalid
     * @throws DatabaseOperationException if an error occurs during saving
     */
    Category createCategory(Category category);

    /**
     * Updates an existing {@link Category}.
     *
     * @param category the category entity containing updated data
     * @return the updated {@link Category}
     * @throws InvalidCategoryException if the category data is invalid
     * @throws CategoryNotFoundException if the category does not exist
     * @throws DatabaseOperationException if an error occurs during update
     */
    Category updateCategory(Category category);

    /**
     * Retrieves a {@link Category} by its unique ID.
     *
     * @param id the ID of the category
     * @return the {@link Category} if found
     * @throws CategoryNotFoundException if the category with given ID is not found
     */
    Category getCategoryById(Long id);

    /**
     * Retrieves all {@link Category} records from the database.
     *
     * @return list of all categories
     * @throws DatabaseOperationException if an error occurs while fetching data
     */
    List<Category> getAllCategories();

    /**
     * Deletes a {@link Category} by its ID.
     *
     * @param id the ID of the category to delete
     * @throws CategoryNotFoundException if the category with given ID does not exist
     * @throws DatabaseOperationException if an error occurs during deletion
     */
    void deleteCategory(Long id);

    /**
     * Retrieves a {@link Category} by its title.
     *
     * @param title the title of the category
     * @return the {@link Category} with the given title
     * @throws InvalidCategoryException if the title is invalid or blank
     * @throws CategoryNotFoundException if no category exists with the given title
     */
    Category getCategoryByTitle(String title);
}
