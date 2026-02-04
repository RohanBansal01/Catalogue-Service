package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.model.Currency;
import com.solveda.catalogueservice.model.ProductPrice;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing product pricing.
 * <p>
 * Handles creation, updates, expiration, and retrieval
 * of product prices. Supports paginated retrieval of active prices.
 * </p>
 */
public interface ProductPriceService {

    /**
     * Creates a new price for a product.
     *
     * @param productId product identifier
     * @param currency  currency code (e.g. INR, USD)
     * @param amount    price amount
     * @return the created {@link ProductPrice}
     * @throws RuntimeException if product does not exist or validation fails
     */
    ProductPrice createPrice(Long productId, String currency, BigDecimal amount);

    /**
     * Changes the amount of an existing price.
     *
     * @param priceId   price identifier
     * @param newAmount updated price amount
     * @throws RuntimeException if price is not found or amount is invalid
     */
    void changePrice(Long priceId, BigDecimal newAmount);

    /**
     * Expires a price, making it inactive.
     *
     * @param priceId price identifier
     * @throws RuntimeException if price is not found
     */
    void expirePrice(Long priceId);

    /**
     * Retrieves a price by its ID.
     *
     * @param priceId price identifier
     * @return optional containing the price if found
     */
    Optional<ProductPrice> getPrice(Long priceId);

    /**
     * Retrieves all active prices for a product.
     *
     * @param productId product identifier
     * @return list of active prices
     */
    List<ProductPrice> getActivePrices(Long productId);

    /**
     * Retrieves the currently active price for a given product and currency.
     *
     * @param productId the product identifier
     * @param currency  the currency code (ISO format, e.g., "USD", "INR")
     * @return an optional containing the active price if found
     */
    Optional<ProductPrice> getActivePrice(Long productId, Currency currency);

    // ===============================================
    // NEW: Paginated retrieval of active prices
    // ===============================================
    /**
     * Retrieves all active prices for a product with pagination.
     * Active prices are those where {@code validTo IS NULL} or {@code validTo > now}.
     *
     * @param productId the product identifier
     * @param pageable  pagination and sorting information
     * @return paginated response DTO containing active prices
     */
    PaginatedResponseDTO<ProductPrice> getActivePrices(Long productId, Pageable pageable);
}
