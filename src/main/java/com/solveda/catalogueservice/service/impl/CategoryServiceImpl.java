package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.CategoryAlreadyExistsException;
import com.solveda.catalogueservice.exception.CategoryNotFoundException;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidCategoryException;
import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.repository.CategoryRepository;
import com.solveda.catalogueservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link CategoryService} that manages product categories.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Create, update, activate, and deactivate categories.</li>
 *     <li>Retrieve categories by ID or title, or fetch all active categories.</li>
 *     <li>Handles database operations with proper exception handling.</li>
 * </ul>
 * </p>
 * <p>
 * All write operations are transactional. Read operations are marked
 * {@link Transactional#readOnly()} for optimized performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /* =========================
       CREATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Creates a new category with the given title and description.
     * </p>
     *
     * @throws InvalidCategoryException       if validation fails
     * @throws CategoryAlreadyExistsException if a category with the same title exists
     * @throws DatabaseOperationException     if saving to database fails
     */
    @Override
    public Category createCategory(String title, String description) {
        Category category;
        try {
            category = Category.create(title, description);
        } catch (IllegalArgumentException ex) {
            throw new InvalidCategoryException(ex.getMessage());
        }
        return saveCategory(category, "creating");
    }

    /* =========================
       UPDATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Updates an existing category's title and description.
     * </p>
     *
     * @throws CategoryNotFoundException   if category is not found
     * @throws InvalidCategoryException    if validation fails
     * @throws DatabaseOperationException  if saving to database fails
     */
    @Override
    public Category updateCategory(Long id, String newTitle, String newDescription) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));

        try {
            category.rename(newTitle);
            category.changeDescription(newDescription);
        } catch (IllegalArgumentException ex) {
            throw new InvalidCategoryException(ex.getMessage());
        }

        return saveCategory(category, "updating");
    }

    /* =========================
       ACTIVATE / DEACTIVATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Activates the specified category.
     * </p>
     *
     * @throws CategoryNotFoundException  if category is not found
     * @throws DatabaseOperationException if saving to database fails
     */
    @Override
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
        category.activate();
        saveCategory(category, "activating");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deactivates the specified category.
     * </p>
     *
     * @throws CategoryNotFoundException  if category is not found
     * @throws DatabaseOperationException if saving to database fails
     */
    @Override
    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
        category.deactivate();
        saveCategory(category, "deactivating");
    }

    /* =========================
       READ METHODS
       ========================= */

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
    }

    /**
     * Retrieves all active categories in a paginated format.
     * <p>
     * An active category is defined as a {@link Category} with {@code active = true}.
     * The returned {@link org.springframework.data.domain.Page} supports pagination and sorting
     * as defined by the provided {@link Pageable} parameter.
     *
     * @param pageable the pagination and sorting information
     * @return a {@link org.springframework.data.domain.Page} containing active {@link Category} entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllActiveCategories(Pageable pageable) {
        return categoryRepository.findByActiveTrue(pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

    /* =========================
       INTERNAL HELPER
       ========================= */

    /**
     * Saves a category to the database with proper exception handling.
     *
     * @param category  category to save
     * @param operation description of the operation (creating, updating, activating, deactivating)
     * @return saved {@link Category}
     * @throws CategoryAlreadyExistsException if duplicate category exists
     * @throws DatabaseOperationException     if save fails
     */
    private Category saveCategory(Category category, String operation) {
        try {
            return categoryRepository.save(category);
        } catch (DataIntegrityViolationException ex) {
            throw new CategoryAlreadyExistsException(category.getTitle());
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error while " + operation + " category: " + category.getTitle(), ex);
        }
    }
}
