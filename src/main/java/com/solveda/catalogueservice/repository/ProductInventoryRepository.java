package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.ProductInventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link ProductInventory} entities.
 */
@Repository
public interface ProductInventoryRepository extends JpaRepository<ProductInventory, Long> {

    /**
     * Finds the inventory record for a specific product ID.
     *
     * @param productId the ID of the product
     * @return an {@link Optional} containing the {@link ProductInventory} if found, otherwise empty
     */
    Optional<ProductInventory> findByProductId(Long productId);

    /**
     * Retrieves all inventory records with available stock in a paginated format.
     *
     * @param quantity minimum available quantity to filter active products
     * @param pageable pagination information (page number, size, sort)
     * @return a {@link Page} of {@link ProductInventory} records with available stock
     */
    Page<ProductInventory> findAllByAvailableQuantityGreaterThan(int quantity, Pageable pageable);
}
