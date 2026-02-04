package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.Currency;
import com.solveda.catalogueservice.model.ProductPrice;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link ProductPrice} entity.
 * <p>
 * Provides standard CRUD operations inherited from {@link JpaRepository}
 * and custom queries for managing product prices.
 * </p>
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

    /**
     * Retrieves the currently active {@link ProductPrice} record for the given product and currency,
     * applying a <b>pessimistic write lock</b>.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT pp FROM ProductPrice pp
        WHERE pp.productId = :productId
        AND pp.currency = :currency
        AND pp.validTo IS NULL
    """)
    Optional<ProductPrice> findActivePriceForUpdate(Long productId, Currency currency);

    /**
     * Retrieves the active {@link ProductPrice} for a specific product and currency.
     */
    @Query("""
        SELECT pp FROM ProductPrice pp
        WHERE pp.productId = :productId
        AND pp.currency = :currency
        AND pp.validTo IS NULL
    """)
    Optional<ProductPrice> findActivePrice(Long productId, Currency currency);

    // =====================================
    // NEW: Paginated Active Prices
    // =====================================
    /**
     * Retrieves all active prices for a given product with pagination.
     * Active prices are those where {@code validTo IS NULL} or {@code validTo > now}.
     *
     * @param productId the product identifier
     * @param now       the current timestamp
     * @param pageable  pagination and sorting information
     * @return a {@link Page} of active {@link ProductPrice} entities
     */
    Page<ProductPrice> findByProductIdAndValidToIsNullOrValidToAfter(
            Long productId,
            LocalDateTime now,
            Pageable pageable
    );
}
