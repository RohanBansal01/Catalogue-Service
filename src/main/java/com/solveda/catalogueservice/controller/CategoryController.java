package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.payload.ResponseStructure;
import com.solveda.catalogueservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST controller for managing {@link Category} entities.
 * <p>
 * Provides endpoints for creating, updating, retrieving, and deleting categories.
 * All responses are wrapped in {@link ResponseStructure} to ensure a consistent
 * API response format.
 * </p>
 *
 * <p><b>Endpoints:</b></p>
 * <ul>
 *     <li>POST /api/categories → create a new category</li>
 *     <li>PUT /api/categories/{id} → update an existing category</li>
 *     <li>GET /api/categories/{id} → retrieve a category by ID</li>
 *     <li>GET /api/categories → retrieve all categories</li>
 *     <li>DELETE /api/categories/{id} → delete a category by ID</li>
 *     <li>GET /api/categories/title/{title} → retrieve a category by title</li>
 * </ul>
 *
 * <p>Validation annotations are applied to incoming requests using {@link Valid}.</p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Create a new {@link Category}.
     *
     * @param category the category object to create; must be valid
     * @return {@link ResponseEntity} containing {@link ResponseStructure} with the created category
     */
    @PostMapping
    public ResponseEntity<ResponseStructure<Category>> createCategory(@Valid @RequestBody Category category) {
        Category savedCategory = categoryService.createCategory(category);
        ResponseStructure<Category> response = ResponseStructure.<Category>builder()
                .status("success")
                .data(savedCategory)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Update an existing {@link Category}.
     *
     * @param id the ID of the category to update
     * @param category the category object with updated values; must be valid
     * @return {@link ResponseEntity} containing {@link ResponseStructure} with the updated category
     */
    @PutMapping("/{id}")
    public ResponseEntity<ResponseStructure<Category>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody Category category) {
        category.setId(id);
        Category updatedCategory = categoryService.updateCategory(category);
        ResponseStructure<Category> response = ResponseStructure.<Category>builder()
                .status("success")
                .data(updatedCategory)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve a {@link Category} by its ID.
     *
     * @param id the ID of the category to retrieve
     * @return {@link ResponseEntity} containing {@link ResponseStructure} with the found category
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        ResponseStructure<Category> response = ResponseStructure.<Category>builder()
                .status("success")
                .data(category)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all categories.
     *
     * @return {@link ResponseEntity} containing {@link ResponseStructure} with a list of all categories
     */
    @GetMapping
    public ResponseEntity<ResponseStructure<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        ResponseStructure<List<Category>> response = ResponseStructure.<List<Category>>builder()
                .status("success")
                .data(categories)
                .build();
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a {@link Category} by its ID.
     *
     * @param id the ID of the category to delete
     * @return {@link ResponseEntity} with HTTP status 204 (No Content) if deletion is successful
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieve a {@link Category} by its title.
     *
     * @param title the title of the category to retrieve
     * @return {@link ResponseEntity} containing {@link ResponseStructure} with the found category
     */
    @GetMapping("/title/{title}")
    public ResponseEntity<ResponseStructure<Category>> getCategoryByTitle(@PathVariable String title) {
        Category category = categoryService.getCategoryByTitle(title);
        ResponseStructure<Category> response = ResponseStructure.<Category>builder()
                .status("success")
                .data(category)
                .build();
        return ResponseEntity.ok(response);
    }
}
