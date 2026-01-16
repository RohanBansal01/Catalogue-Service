package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long id;

    /* =========================
       References (cross-aggregate)
       ========================= */

    @Column(name = "product_id", nullable = false)
    @ToString.Include
    private Long productId; // reference only, NOT association

    /* =========================
       Value
       ========================= */

    @Column(length = 3, nullable = false)
    @ToString.Include
    private String currency;

    @Column(precision = 19, scale = 4, nullable = false)
    @ToString.Include
    private BigDecimal amount;

    /* =========================
       Validity window
       ========================= */

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    /* =========================
       Factory
       ========================= */

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
       Behavior (DDD)
       ========================= */

    public void changeAmount(BigDecimal newAmount) {
        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price amount must be positive");
        }
        this.amount = newAmount;
    }

    public void expire() {
        if (this.validTo != null) {
            return; // idempotent
        }
        this.validTo = LocalDateTime.now();
    }


    public boolean isActiveNow() {
        return isActiveAt(LocalDateTime.now());
    }


    public boolean isActiveAt(LocalDateTime moment) {
        if (moment == null) {
            throw new IllegalArgumentException("Moment must not be null");
        }
        return !moment.isBefore(validFrom)
                && (validTo == null || moment.isBefore(validTo));
    }
}
