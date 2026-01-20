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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional // Default for write operations
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    /* =========================
       CREATE
       ========================= */
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
    @Override
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));

        category.activate();
        saveCategory(category, "activating");
    }

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
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category with ID " + id + " not found"));
    }


    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryByTitle(String title) {
        return categoryRepository.findByTitle(title);
    }

    /* =========================
       INTERNAL HELPER
       ========================= */
    private Category saveCategory(Category category, String operation) {
        try {
            return categoryRepository.save(category);
        }
        catch (DataIntegrityViolationException ex) {
            // Duplicate category (UNIQUE constraint violation)
            throw new CategoryAlreadyExistsException(category.getTitle());
        }
        catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error while " + operation + " category: " + category.getTitle(), ex);
        }
    }
}
