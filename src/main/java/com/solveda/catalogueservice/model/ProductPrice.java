package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a price entry for a product.
 * <p>
 * This entity tracks the price of a product in a specific currency over a validity window.
 * It follows a DDD-style design where {@code productId} is a reference to the Product aggregate
 * but not a direct JPA association.
 * </p>
 */
@Entity
@Table(name = "product_prices")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductPrice {

    /* =========================
       Identity
       ========================= */

    /** Unique identifier of the price entry. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       References (cross-aggregate)
       ========================= */

    /** Product ID that this price belongs to (aggregate reference). */
    @Column(name = "product_id", nullable = false)
    @ToString.Include
    private Long productId;

    /* =========================
       Value
       ========================= */

    /** Currency code (ISO 4217, e.g., USD, INR). */
    @Column(length = 3, nullable = false)
    @ToString.Include
    private String currency;

    /** Price amount in the given currency, must be positive. */
    @Column(precision = 19, scale = 4, nullable = false)
    @ToString.Include
    private BigDecimal amount;

    /* =========================
       Validity window
       ========================= */

    /** Timestamp when this price becomes valid. */
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    /** Timestamp when this price expires. Null if still active. */
    @Column(name = "valid_to")
    private LocalDateTime validTo;

    /* =========================
       Factory Method
       ========================= */

    /**
     * Factory method to create a new {@link ProductPrice}.
     *
     * @param productId the product ID, must not be null
     * @param currency  currency code, must not be null or blank
     * @param amount    price amount, must be positive
     * @return a new {@link ProductPrice} instance
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public static ProductPrice create(
            Long productId,
            String currency,
            BigDecimal amount
    ) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId must be provided");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price amount must be positive");
        }

        ProductPrice price = new ProductPrice();
        price.productId = productId;
        price.currency = currency;
        price.amount = amount;
        price.validFrom = LocalDateTime.now();

        return price;
    }

    /* =========================
       Domain Behavior
       ========================= */

    /**
     * Updates the price amount.
     *
     * @param newAmount new price amount, must be positive
     * @throws IllegalArgumentException if newAmount is null or <= 0
     */
    public void changeAmount(BigDecimal newAmount) {
        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price amount must be positive");
        }
        this.amount = newAmount;
    }

    /**
     * Expires this price by setting {@code validTo} to current timestamp.
     * <p>Idempotent: calling multiple times has the same effect as calling once.</p>
     */
    public void expire() {
        if (this.validTo != null) {
            return; // idempotent
        }
        this.validTo = LocalDateTime.now();
    }

    /**
     * Checks if this price is currently active (valid at the present moment).
     *
     * @return true if current time is within validity window
     */
    public boolean isActiveNow() {
        return isActiveAt(LocalDateTime.now());
    }

    /**
     * Checks if this price is active at the given moment.
     *
     * @param moment timestamp to check, must not be null
     * @return true if {@code moment} is between validFrom (inclusive) and validTo (exclusive)
     * @throws IllegalArgumentException if moment is null
     */
    public boolean isActiveAt(LocalDateTime moment) {
        if (moment == null) {
            throw new IllegalArgumentException("Moment must not be null");
        }
        return !moment.isBefore(validFrom)
                && (validTo == null || moment.isBefore(validTo));
    }
}
