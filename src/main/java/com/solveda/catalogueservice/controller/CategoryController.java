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

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(
            @Valid @RequestBody CategoryRequestDTO request) {

        Category category =
                categoryService.createCategory(
                        request.title(),
                        request.description()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toResponse(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO request) {

        Category category =
                categoryService.updateCategory(
                        id,
                        request.title(),
                        request.description()
                );

        return ResponseEntity.ok(toResponse(category));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        categoryService.activateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        categoryService.deactivateCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getById(@PathVariable Long id) {
        Category category =
                categoryService.getCategoryById(id);
        return ResponseEntity.ok(toResponse(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getActive() {
        return ResponseEntity.ok(
                categoryService.getAllActiveCategories()
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    private CategoryResponseDTO toResponse(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getTitle(),
                category.getDescription(),
                category.isActive()
        );
    }
}
