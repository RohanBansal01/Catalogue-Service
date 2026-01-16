package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import com.solveda.catalogueservice.model.Product;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity.
 * Provides CRUD operations and custom queries for Product.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

List<Product> findByActiveTrue();
List<Product> findByCategoryId(Long categoryId);

    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);
}
