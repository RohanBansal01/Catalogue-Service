package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * Represents the inventory for a specific product.
 * <p>
 * This entity tracks available and reserved stock quantities for a product
 * and provides domain behaviors for reserving, releasing, and clearing stock.
 * </p>
 *
 * <p>
 * The {@code productId} serves as the shared identity with the {@link Product} aggregate.
 * </p>
 */
@Entity
@Table(name = "product_inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductInventory {

    /* =========================
       Identity
       ========================= */

    /** Product ID associated with this inventory. Shared identity with Product aggregate. */
    @Id
    @Column(name = "product_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long productId;

    /* =========================
       State
       ========================= */

    /** Number of units available for sale. Cannot be negative. */
    @Column(name = "available_quantity", nullable = false)
    @ToString.Include
    private int availableQuantity;

    /** Number of units reserved but not yet consumed. Cannot be negative. */
    @Column(name = "reserved_quantity", nullable = false)
    @ToString.Include
    private int reservedQuantity;

    /** Timestamp of last inventory update. */
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    /** Version for optimistic locking. */
    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    /* =========================
       Factory Method
       ========================= */

    /**
     * Factory method to create a new {@link ProductInventory}.
     *
     * @param productId       the product ID this inventory belongs to, must not be null
     * @param initialQuantity initial available stock quantity, must be >= 0
     * @return a fully initialized {@link ProductInventory} instance
     * @throws IllegalArgumentException if productId is null or initialQuantity < 0
     */
    public static ProductInventory create(Long productId, int initialQuantity) {
        if (productId == null) {
            throw new IllegalArgumentException("ProductId must not be null");
        }
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Initial quantity cannot be negative");
        }

        ProductInventory inventory = new ProductInventory();
        inventory.productId = productId;
        inventory.availableQuantity = initialQuantity;
        inventory.reservedQuantity = 0;
        inventory.lastUpdated = LocalDateTime.now();
        return inventory;
    }

    /* =========================
       Domain Behavior
       ========================= */

    /**
     * Reserves a specified quantity of stock.
     * <p>Not idempotent; calling multiple times changes state each time.</p>
     *
     * @param quantity number of units to reserve, must be positive
     * @throws IllegalArgumentException if quantity <= 0
     * @throws IllegalStateException    if available stock is insufficient
     */
    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Reserve quantity must be positive");
        }
        if (availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient available stock");
        }

        this.availableQuantity -= quantity;
        this.reservedQuantity += quantity;
        touch();
    }

    /**
     * Releases a previously reserved quantity back to available stock.
     * <p>Not idempotent; calling multiple times changes state each time.</p>
     *
     * @param quantity number of units to release, must be positive
     * @throws IllegalArgumentException if quantity <= 0
     * @throws IllegalStateException    if quantity exceeds reserved stock
     */
    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Release quantity must be positive");
        }
        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Cannot release more than reserved");
        }

        this.reservedQuantity -= quantity;
        this.availableQuantity += quantity;
        touch();
    }

    /**
     * Clears all reserved stock, returning it to available stock.
     * <p>Idempotent; calling multiple times has the same effect as calling once.</p>
     */
    public void clearReservations() {
        if (this.reservedQuantity == 0) {
            return; // idempotent
        }

        this.availableQuantity += this.reservedQuantity;
        this.reservedQuantity = 0;
        touch();
    }

    /**
     * Returns the current available stock quantity.
     *
     * @return available stock
     */
    public int availableStock() {
        return availableQuantity;
    }

    /* =========================
       Internal Helper
       ========================= */

    /** Updates the lastUpdated timestamp to the current time. */
    private void touch() {
        this.lastUpdated = LocalDateTime.now();
    }
}
