package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link ProductInventory} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and custom queries for managing product inventory.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     {@code
 *     Optional<ProductInventory> inventory = inventoryRepository.findByProductId(123L);
 *     }
 * </pre>
 * </p>
 *
 * @see JpaRepository
 * @see ProductInventory
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
}
