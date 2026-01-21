package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.PriceRequestDTO;
import com.solveda.catalogueservice.dto.PriceResponseDTO;
import com.solveda.catalogueservice.model.ProductPrice;
import com.solveda.catalogueservice.service.ProductPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing product prices.
 * <p>
 * Provides endpoints to create, update, expire, and retrieve product prices.
 * Supports retrieving both individual prices and all active prices for a product.
 * </p>
 *
 * <p>All operations delegate to {@link ProductPriceService} for business logic and persistence.</p>
 */
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    /** Service handling product price operations */
    private final ProductPriceService priceService;

    /**
     * Creates a new price record for a product.
     *
     * @param request {@link PriceRequestDTO} containing product ID, currency, and amount
     * @return {@link ResponseEntity} with status 200 OK and created {@link PriceResponseDTO}
     */
    @PostMapping
    public ResponseEntity<PriceResponseDTO> create(@Valid @RequestBody PriceRequestDTO request) {
        ProductPrice price = priceService.createPrice(
                request.productId(),
                request.currency(),
                request.amount()
        );
        return ResponseEntity.ok(toResponse(price));
    }

    /**
     * Changes the amount of an existing price.
     * <p>
     * Updates the price amount for the given {@code priceId}. Does not create a new record.
     * </p>
     *
     * @param priceId the ID of the price to update
     * @param amount  the new amount as a string (converted to BigDecimal)
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{priceId}/change")
    public ResponseEntity<Void> change(@PathVariable Long priceId, @RequestParam String amount) {
        priceService.changePrice(priceId, new java.math.BigDecimal(amount));
        return ResponseEntity.noContent().build();
    }

    /**
     * Expires a price.
     * <p>
     * Sets the {@code validTo} timestamp of the price to the current time.
     * Idempotent: calling multiple times has no additional effect.
     * </p>
     *
     * @param priceId the ID of the price to expire
     * @return {@link ResponseEntity} with status 204 No Content
     */
    @PostMapping("/{priceId}/expire")
    public ResponseEntity<Void> expire(@PathVariable Long priceId) {
        priceService.expirePrice(priceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a price by its ID.
     *
     * @param priceId the ID of the price
     * @return {@link ResponseEntity} with status 200 OK and {@link PriceResponseDTO}
     */
    @GetMapping("/{priceId}")
    public ResponseEntity<PriceResponseDTO> get(@PathVariable Long priceId) {
        ProductPrice price = priceService.getPrice(priceId).orElseThrow();
        return ResponseEntity.ok(toResponse(price));
    }

    /**
     * Retrieves all active prices for a given product.
     * <p>
     * Only returns prices where {@code validTo} is null or in the future.
     * </p>
     *
     * @param productId the ID of the product
     * @return {@link ResponseEntity} with status 200 OK and list of {@link PriceResponseDTO}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PriceResponseDTO>> getActive(@PathVariable Long productId) {
        return ResponseEntity.ok(
                priceService.getActivePrices(productId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

    /**
     * Converts {@link ProductPrice} entity to {@link PriceResponseDTO}.
     *
     * @param price the price entity
     * @return DTO representation of the price
     */
    private PriceResponseDTO toResponse(ProductPrice price) {
        return new PriceResponseDTO(
                price.getId(),
                price.getProductId(),
                price.getCurrency(),
                price.getAmount(),
                price.isActiveNow()
        );
    }
}
