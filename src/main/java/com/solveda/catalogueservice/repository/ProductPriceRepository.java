package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for {@link ProductPrice} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and custom queries for managing product prices.
 * </p>
 *
 * <p>
 * Typical usage:
 * <pre>
 *     {@code
 *     List<ProductPrice> allPrices = priceRepository.findByProductId(123L);
 *     List<ProductPrice> activePrices = priceRepository.findByProductIdAndValidToIsNull(123L);
 *     }
 * </pre>
 * </p>
 *
 * @see JpaRepository
 * @see ProductPrice
 */
@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    /**
     * Finds all prices for a specific product.
     *
     * @param productId the ID of the product
     * @return a {@link List} of {@link ProductPrice} entities for the product
     */
    List<ProductPrice> findByProductId(Long productId);

    /**
     * Finds all currently active prices for a specific product.
     * <p>
     * Active prices are those where {@code validTo} is null.
     * </p>
     *
     * @param productId the ID of the product
     * @return a {@link List} of active {@link ProductPrice} entities
     */
    List<ProductPrice> findByProductIdAndValidToIsNull(Long productId);
}
