package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.dto.CategoryRequestDTO;
import com.solveda.catalogueservice.dto.CategoryResponseDTO;
import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link Category} entities.
 * <p>
 * Provides endpoints to create, update, activate, deactivate, and retrieve categories.
 * Supports pagination and sorting for listing active categories.
 * Responses are wrapped in {@link ResponseEntity} with standardized DTOs.
 * </p>
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new category.
     *
     * @param request {@link CategoryRequestDTO} containing title and description
     * @return {@link ResponseEntity} containing {@link CategoryResponseDTO} with HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryRequestDTO request) {
        Category category = categoryService.createCategory(request.title(), request.description());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    /**
     * Updates an existing category.
     *
     * @param id      the ID of the category to update
     * @param request {@link CategoryRequestDTO} containing updated title and description
     * @return {@link ResponseEntity} containing {@link CategoryResponseDTO} with HTTP status 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryRequestDTO request) {
        Category category = categoryService.updateCategory(id, request.title(), request.description());
        return ResponseEntity.ok(toResponse(category));
    }

    /**
     * Activates a category.
     *
     * @param id the ID of the category to activate
     * @return {@link ResponseEntity} with HTTP status 204 NO CONTENT
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deactivates a category.
     *
     * @param id the ID of the category to deactivate
     * @return {@link ResponseEntity} with HTTP status 204 NO CONTENT
     */
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id the ID of the category
     * @return {@link ResponseEntity} containing {@link CategoryResponseDTO} with HTTP status 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(toResponse(category));
    }

    /**
     * Retrieves all active categories with pagination and sorting.
     *
     * @param page      zero-based page index (default 0)
     * @param size      number of items per page (default 10)
     * @param sortBy    field to sort by (default "title")
     * @param direction sort direction, ASC or DESC (default ASC)
     * @return {@link ResponseEntity} containing {@link PaginatedResponseDTO} of active categories
     */
    @GetMapping
    public ResponseEntity<PaginatedResponseDTO<CategoryResponseDTO>> getActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Category> categoryPage = categoryService.getAllActiveCategories(pageable);

        return ResponseEntity.ok(toPaginatedResponse(categoryPage));
    }

    // =========================
    // HELPER METHODS
    // =========================

    /**
     * Converts a {@link Category} entity into a {@link CategoryResponseDTO}.
     *
     * @param category the category entity
     * @return the corresponding {@link CategoryResponseDTO}
     */
    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getTitle(),
                category.getDescription(),
                category.isActive()
        );
    }

    /**
     * Converts a {@link Page} of {@link Category} entities into a {@link PaginatedResponseDTO}.
     *
     * @param categoryPage the page of categories
     * @return {@link PaginatedResponseDTO} containing category DTOs and pagination metadata
     */
    private PaginatedResponseDTO<CategoryResponseDTO> toPaginatedResponse(Page<Category> categoryPage) {
        List<CategoryResponseDTO> content = categoryPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages(),
                categoryPage.isFirst(),
                categoryPage.isLast()
        );
    }
}
