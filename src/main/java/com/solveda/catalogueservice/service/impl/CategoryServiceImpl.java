package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.CategoryNotFoundException;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidCategoryException;
import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.repository.CategoryRepository;
import com.solveda.catalogueservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CategoryService} that provides CRUD operations
 * and validation logic for {@link Category} entities.
 * <p>
 * This class acts as a business layer between controllers and repositories,
 * handling data validation, exception translation, and persistence operations.
 * </p>
 *
 * <p><b>Responsibilities:</b></p>
 * <ul>
 *     <li>Validating {@link Category} attributes before persistence.</li>
 *     <li>Handling repository operations with proper exception translation.</li>
 *     <li>Throwing domain-specific exceptions for invalid or missing data.</li>
 * </ul>
 *
 * @see com.solveda.catalogueservice.repository.CategoryRepository
 * @see com.solveda.catalogueservice.exception.CategoryNotFoundException
 * @see com.solveda.catalogueservice.exception.InvalidCategoryException
 * @see com.solveda.catalogueservice.exception.DatabaseOperationException
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * Creates a new {@link Category} after validation.
     *
     * @param category the category entity to be created
     * @return the persisted {@link Category} entity
     * @throws InvalidCategoryException if the category is null or contains invalid data
     * @throws DatabaseOperationException if a database error occurs while saving
     */
    @Override
    public Category createCategory(Category category) {
        validateCategory(category);
        return saveCategory(category, "saving");
    }

    /**
     * Updates an existing {@link Category} after validation.
     * <p>
     * Ensures the category exists before performing the update.
     * </p>
     *
     * @param category the category entity containing updated values
     * @return the updated {@link Category}
     * @throws InvalidCategoryException if category data is invalid
     * @throws CategoryNotFoundException if the category ID does not exist
     * @throws DatabaseOperationException if a database error occurs while updating
     */
    @Override
    public Category updateCategory(Category category) {
        validateCategory(category);

        Long id = category.getId();
        Optional.ofNullable(id)
                .filter(categoryRepository::existsById)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));

        return saveCategory(category, "updating");
    }

    /**
     * Retrieves a {@link Category} by its unique ID.
     *
     * @param id the ID of the category
     * @return the {@link Category} if found
     * @throws CategoryNotFoundException if no category exists with the provided ID
     */
    @Override
    public Category getCategoryById(Long id) {
        return Optional.ofNullable(id)
                .flatMap(categoryRepository::findById)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
    }

    /**
     * Retrieves all {@link Category} records from the database.
     *
     * @return a list of all {@link Category} entities
     * @throws DatabaseOperationException if a database access error occurs
     */
    @Override
    public List<Category> getAllCategories() {
        try {
            return categoryRepository.findAll();
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while fetching categories", ex);
        }
    }

    /**
     * Deletes a {@link Category} by its ID.
     * <p>
     * Ensures the category exists before attempting deletion.
     * </p>
     *
     * @param id the ID of the category to delete
     * @throws CategoryNotFoundException if the category with given ID does not exist
     * @throws DatabaseOperationException if an error occurs during deletion
     */
    @Override
    public void deleteCategory(Long id) {
        Optional.ofNullable(id)
                .filter(categoryRepository::existsById)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found for deletion"));

        try {
            categoryRepository.deleteById(id);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while deleting category ID: " + id, ex);
        }
    }

    /**
     * Retrieves a {@link Category} by its title.
     *
     * @param title the category title to search for
     * @return the {@link Category} if found
     * @throws InvalidCategoryException if the title is blank or null
     * @throws CategoryNotFoundException if no category exists with the given title
     */
    @Override
    public Category getCategoryByTitle(String title) {
        return Optional.ofNullable(title)
                .filter(t -> !t.isBlank())
                .map(t -> Optional.ofNullable(categoryRepository.findByTitle(t))
                        .orElseThrow(() -> new CategoryNotFoundException("Category with title '" + t + "' not found")))
                .orElseThrow(() -> new InvalidCategoryException("Category title cannot be blank"));
    }

    /**
     * Validates a {@link Category}'s attributes against business and data constraints.
     *
     * @param category the category to validate
     * @throws InvalidCategoryException if validation fails for title, description, or null object
     */
    private void validateCategory(Category category) {
        Optional.ofNullable(category).orElseThrow(() -> new InvalidCategoryException("Category cannot be null"));

        Optional.ofNullable(category.getTitle())
                .filter(t -> !t.isBlank())
                .orElseThrow(() -> new InvalidCategoryException("Category title cannot be blank"));

        if (category.getTitle().length() > 100) {
            throw new InvalidCategoryException("Category title must not exceed 100 characters");
        }

        if (category.getDescription() != null && category.getDescription().length() > 500) {
            throw new InvalidCategoryException("Category description must not exceed 500 characters");
        }
    }

    /**
     * Persists a {@link Category} to the database with exception translation.
     * <p>
     * Used internally by both {@link #createCategory(Category)} and {@link #updateCategory(Category)}.
     * </p>
     *
     * @param category  the category to be saved
     * @param operation a descriptive name of the operation for error reporting
     * @return the persisted {@link Category}
     * @throws DatabaseOperationException if a persistence error occurs
     */
    private Category saveCategory(Category category, String operation) {
        try {
            return categoryRepository.save(category);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while " + operation + " category: " + category.getTitle(), ex);
        }
    }
}
