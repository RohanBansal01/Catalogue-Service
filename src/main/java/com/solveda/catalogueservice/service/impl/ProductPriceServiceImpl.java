package com.solveda.catalogueservice.service.impl;

import com.solveda.catalogueservice.exception.DatabaseOperationException;
import com.solveda.catalogueservice.exception.InvalidPriceException;
import com.solveda.catalogueservice.exception.ProductNotFoundException;
import com.solveda.catalogueservice.model.ProductPrice;
import com.solveda.catalogueservice.repository.ProductPriceRepository;
import com.solveda.catalogueservice.service.ProductPriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ProductPriceService} that manages product pricing.
 * <p>
 * Responsibilities:
 * <ul>
 *     <li>Create, update, and expire product prices.</li>
 *     <li>Retrieve individual prices or active prices for a product.</li>
 *     <li>Handles database operations with exception handling.</li>
 * </ul>
 * </p>
 * <p>
 * All write operations are transactional. Read operations are marked
 * {@link Transactional#readOnly()} for optimized performance.
 * </p>
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository priceRepository;

    /* =========================
       CREATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Validates and creates a new price for a product.
     * </p>
     *
     * @throws InvalidPriceException       if validation fails
     * @throws DatabaseOperationException  if saving to database fails
     */
    @Override
    public ProductPrice createPrice(Long productId, String currency, BigDecimal amount) {
        ProductPrice price;
        try {
            price = ProductPrice.create(productId, currency, amount);
        } catch (IllegalArgumentException ex) {
            throw new InvalidPriceException(ex.getMessage());
        }
        return savePrice(price, "creating");
    }

    /* =========================
       UPDATE
       ========================= */

    /**
     * {@inheritDoc}
     * <p>
     * Changes the amount of an existing price.
     * </p>
     *
     * @throws ProductNotFoundException    if price is not found
     * @throws InvalidPriceException       if validation fails
     * @throws DatabaseOperationException  if saving to database fails
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
     * {@inheritDoc}
     * <p>
     * Expires a price, making it inactive.
     * </p>
     *
     * @throws ProductNotFoundException    if price is not found
     * @throws DatabaseOperationException  if saving to database fails
     */
    @Override
    public void expirePrice(Long priceId) {
        ProductPrice price = findPriceOrThrow(priceId);
        price.expire();
        savePrice(price, "expiring price");
    }

    /* =========================
       READ
       ========================= */

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPrice> getPrice(Long priceId) {
        return priceRepository.findById(priceId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves all active prices for a given product.
     * </p>
     *
     * @throws DatabaseOperationException if database fetch fails
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProductPrice> getActivePrices(Long productId) {
        try {
            return priceRepository.findByProductIdAndValidToIsNull(productId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error fetching active prices for product ID " + productId, ex);
        }
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */

    /**
     * Finds a price by ID or throws {@link ProductNotFoundException}.
     *
     * @param priceId price identifier
     * @return found {@link ProductPrice}
     * @throws ProductNotFoundException if price is not found
     */
    private ProductPrice findPriceOrThrow(Long priceId) {
        return priceRepository.findById(priceId)
                .orElseThrow(() -> new ProductNotFoundException("Price with ID " + priceId + " not found"));
    }

    /**
     * Saves a price to the database with error handling.
     *
     * @param price     price to save
     * @param operation description of the operation (creating, updating, expiring)
     * @return saved {@link ProductPrice}
     * @throws DatabaseOperationException if save fails
     */
    private ProductPrice savePrice(ProductPrice price, String operation) {
        try {
            return priceRepository.save(price);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException(
                    "Error while " + operation + " for price ID " + price.getId(), ex);
        }
    }
}
