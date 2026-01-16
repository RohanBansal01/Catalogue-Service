package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * <pre>
 *  Repository interface for Category entity.
 *  Provides CRUD operations and custom queries for Category.
 * </pre>
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByTitle(String title);

    List<Category> findByActiveTrue();
}
