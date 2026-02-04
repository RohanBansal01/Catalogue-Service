package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidPriceException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.Currency;
import com.solveda.catalogueservice.model.ProductPrice;
import com.solveda.catalogueservice.repository.ProductPriceRepository;
import com.solveda.catalogueservice.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ProductPriceService} that manages product pricing.
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Creating, updating, and expiring product prices.</li>
 *     <li>Retrieving individual prices or active prices for a product.</li>
 *     <li>Supporting paginated retrieval of active prices.</li>
 *     <li>Handling database operations with proper exception handling.</li>
 * </ul>
 * </p>
 *
 * <p>
 * All write operations are transactional. Read operations are marked as {@link Transactional#readOnly()}
 * for performance optimization.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository priceRepository;

    // =========================
    // CREATE
    // =========================

    /**
     * Creates a new price for a product.
     * <p>
     * If an active price already exists for the same product and currency, it will be expired
     * before creating the new one.
     * </p>
     *
     * @param productId the product identifier
     * @param currency  the currency code (e.g., "USD", "INR")
     * @param amount    the price amount
     * @return the newly created {@link ProductPrice}
     * @throws InvalidPriceException      if the currency code is invalid or an active price already exists
     * @throws DatabaseOperationException if a database error occurs during save
     */
    @Override
    public ProductPrice createPrice(Long productId, String currency, BigDecimal amount) {
        Currency currencyEnum;
        try {
            currencyEnum = Currency.fromCode(currency);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPriceException("Invalid currency code: " + currency);
        }

        Optional<ProductPrice> existingActive = priceRepository.findActivePriceForUpdate(productId, currencyEnum);

        existingActive.ifPresent(activePrice -> {
            activePrice.expire();
            priceRepository.saveAndFlush(activePrice);
        });

        ProductPrice newPrice = ProductPrice.create(productId, currencyEnum, amount);

        try {
            return priceRepository.save(newPrice);
        } catch (DataIntegrityViolationException ex) {
            throw new InvalidPriceException(
                    String.format("Active price already exists for productId=%d, currency=%s", productId, currencyEnum)
            );
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while creating price for productId=" + productId, ex);
        }
    }

    // =========================
    // UPDATE
    // =========================

    /**
     * Updates the amount of an existing price.
     *
     * @param priceId   the price identifier
     * @param newAmount the new price amount
     * @throws ProductNotFoundException   if the price with the given ID does not exist
     * @throws InvalidPriceException      if the new amount is invalid
     * @throws DatabaseOperationException if a database error occurs
     */
    @Override
    public void changePrice(Long priceId, BigDecimal newAmount) {
        ProductPrice price = findPriceOrThrow(priceId);
        try {
            price.changeAmount(newAmount);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPriceException(ex.getMessage());
        }
        savePrice(price, "changing amount");
    }

    /**
     * Expires an existing price.
     *
     * @param priceId the price identifier
     * @throws ProductNotFoundException   if the price with the given ID does not exist
     * @throws DatabaseOperationException if a database error occurs
     */
    @Override
    public void expirePrice(Long priceId) {
        ProductPrice price = findPriceOrThrow(priceId);
        price.expire();
        savePrice(price, "expiring price");
    }

    // =========================
    // READ
    // =========================

    /**
     * Retrieves a price by its ID.
     *
     * @param priceId the price identifier
     * @return an {@link Optional} containing the {@link ProductPrice} if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPrice> getPrice(Long priceId) {
        return priceRepository.findById(priceId);
    }

    /**
     * Retrieves all currently active prices for a given product.
     *
     * @param productId the product identifier
     * @return list of active {@link ProductPrice} entities
     * @throws DatabaseOperationException if a database error occurs
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductPrice> getActivePrices(Long productId) {
        try {
            return priceRepository.findByProductIdAndValidToIsNull(productId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error fetching active prices for productId=" + productId, ex);
        }
    }

    /**
     * Retrieves the currently active price for a product in a specific currency.
     *
     * @param productId the product identifier
     * @param currency  the currency
     * @return an {@link Optional} containing the active {@link ProductPrice}
     * @throws DatabaseOperationException if a database error occurs
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPrice> getActivePrice(Long productId, Currency currency) {
        try {
            return priceRepository.findActivePrice(productId, currency);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    String.format("Error fetching active price for productId=%d, currency=%s", productId, currency), ex
            );
        }
    }

    // =========================
    // PAGINATED ACTIVE PRICES
    // =========================

    /**
     * Retrieves currently active prices for a product in a paginated format.
     *
     * @param productId the product identifier
     * @param pageable  pagination and sorting information
     * @return a {@link PaginatedResponseDTO} containing the paginated {@link ProductPrice} entities
     * @throws DatabaseOperationException if a database error occurs
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedResponseDTO<ProductPrice> getActivePrices(Long productId, Pageable pageable) {
        try {
            LocalDateTime now = LocalDateTime.now();
            Page<ProductPrice> page = priceRepository.findByProductIdAndValidToIsNullOrValidToAfter(
                    productId, now, pageable
            );

            return new PaginatedResponseDTO<>(
                    page.getContent(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages(),
                    page.isFirst(),
                    page.isLast()
            );

        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error fetching paginated active prices for productId=" + productId, ex
            );
        }
    }

    // =========================
    // INTERNAL HELPERS
    // =========================

    /**
     * Finds a price by ID or throws {@link ProductNotFoundException}.
     *
     * @param priceId the price identifier
     * @return the {@link ProductPrice} entity
     * @throws ProductNotFoundException if the price does not exist
     */
    private ProductPrice findPriceOrThrow(Long priceId) {
        return priceRepository.findById(priceId)
                .orElseThrow(() -> new ProductNotFoundException("Price with ID " + priceId + " not found"));
    }

    /**
     * Saves a {@link ProductPrice} entity with exception handling.
     *
     * @param price     the price entity to save
     * @param operation description of the operation for logging/error messages
     * @return saved {@link ProductPrice} entity
     * @throws DatabaseOperationException if a database error occurs
     */
    private ProductPrice savePrice(ProductPrice price, String operation) {
        try {
            return priceRepository.save(price);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error while " + operation + " for price ID " + price.getId(), ex
            );
        }
    }
}
