package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.CategoryRequestDTO;
import com.solveda.catalogueservice.dto.CategoryResponseDTO;
import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link Category} entities.
 * <p>
 * Provides endpoints to create, update, activate, deactivate, and retrieve categories.
 * Responses are mapped to {@link CategoryResponseDTO} for consistent API output.
 * </p>
 *
 * <p>All category operations delegate to {@link CategoryService} which
 * encapsulates business logic, validation, and persistence.</p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    /** Service that handles all category business logic */
    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param request the {@link CategoryRequestDTO} containing title and optional description
     * @return {@link ResponseEntity} with status 201 Created and the created {@link CategoryResponseDTO}
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request) {
        Category category = categoryService.createCategory(request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    /**
     * Updates an existing category by ID.
     *
     * @param id the ID of the category to update
     * @param request the {@link CategoryRequestDTO} containing updated title and description
     * @return {@link ResponseEntity} with status 200 OK and the updated {@link CategoryResponseDTO}
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryRequestDTO request) {
        Category category = categoryService.updateCategory(id, request.title(), request.description());
        return ResponseEntity.ok(toResponse(category));
    }

    /**
     * Activates a category by ID.
     * <p>
     * Idempotent operation: activating an already active category has no effect.
     * </p>
     *
     * @param id the ID of the category to activate
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a category by ID.
     * <p>
     * Idempotent operation: deactivating an already inactive category has no effect.
     * </p>
     *
     * @param id the ID of the category to deactivate
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a category by ID.
     *
     * @param id the ID of the category
     * @return {@link ResponseEntity} with status 200 OK and the {@link CategoryResponseDTO}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(toResponse(category));
    }

    /**
     * Retrieves all active categories.
     *
     * @return {@link ResponseEntity} with status 200 OK and a list of {@link CategoryResponseDTO}
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getActive() {
        List<CategoryResponseDTO> response = categoryService.getAllActiveCategories()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    /**
     * Converts a {@link Category} entity to {@link CategoryResponseDTO}.
     *
     * @param category the entity to convert
     * @return the DTO representation of the category
     */
    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getTitle(),
                category.getDescription(),
                category.isActive()
        );
    }
}
