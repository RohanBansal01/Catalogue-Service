package com.solveda.catalogueservice.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_inventory")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ProductInventory {

    @Id
    @Column(name = "product_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long productId; // Shared identity (Product aggregate owns identity)

    @Column(name = "available_quantity", nullable = false)
    @ToString.Include
    private int availableQuantity;

    @Column(name = "reserved_quantity", nullable = false)
    @ToString.Include
    private int reservedQuantity;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;

    /* =========================
       Factory / Constructor
       ========================= */

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
     * NOT idempotent
     * Calling twice changes state twice
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
     * NOT idempotent
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
     * Idempotent
     * Calling this multiple times with same state = same result
     */
    public void clearReservations() {
        if (this.reservedQuantity == 0) {
            return; // idempotent
        }

        this.availableQuantity += this.reservedQuantity;
        this.reservedQuantity = 0;
        touch();
    }

    public int availableStock() {
        return availableQuantity;
    }

    /* =========================
       Internal helper
       ========================= */

    private void touch() {
        this.lastUpdated = LocalDateTime.now();
    }
}
