package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Category createCategory(String title, String description);

    Category updateCategory(Long id, String newTitle, String newDescription);

    void activateCategory(Long id);

    void deactivateCategory(Long id);

    Category getCategoryById(Long id);

    Optional<Category> getCategoryByTitle(String title);


    List<Category> getAllActiveCategories();
}
