package com.solveda.catalogueservice.controller;

import com.solveda.catalogueservice.dto.PaginatedResponseDTO;
import com.solveda.catalogueservice.dto.PriceRequestDTO;
import com.solveda.catalogueservice.dto.PriceResponseDTO;
import com.solveda.catalogueservice.exception.PriceNotFoundException;
import com.solveda.catalogueservice.model.Currency;
import com.solveda.catalogueservice.model.ProductPrice;
import com.solveda.catalogueservice.service.ProductPriceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing {@link ProductPrice} entities.
 * <p>
 * Provides endpoints to:
 * <ul>
 *     <li>Create new product prices.</li>
 *     <li>Change or expire existing prices.</li>
 *     <li>Retrieve a price by ID.</li>
 *     <li>Retrieve all active prices for a product, optionally paginated.</li>
 *     <li>Retrieve the currently active price for a product in a given currency.</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final ProductPriceService priceService;

    /**
     * Creates a new price for a product.
     *
     * @param request the {@link PriceRequestDTO} containing product ID, currency, and amount
     * @return the created {@link PriceResponseDTO} with generated ID and active status
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
     *
     * @param priceId the ID of the price to update
     * @param amount  the new price amount as string
     * @return 204 No Content on success
     */
    @PostMapping("/{priceId}/change")
    public ResponseEntity<Void> change(@PathVariable Long priceId, @RequestParam String amount) {
        priceService.changePrice(priceId, new java.math.BigDecimal(amount));
        return ResponseEntity.noContent().build();
    }

    /**
     * Expires an existing price, marking it as inactive.
     *
     * @param priceId the ID of the price to expire
     * @return 204 No Content on success
     */
    @PostMapping("/{priceId}/expire")
    public ResponseEntity<Void> expire(@PathVariable Long priceId) {
        priceService.expirePrice(priceId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a price by its ID.
     *
     * @param priceId the price identifier
     * @return {@link PriceResponseDTO} for the requested price
     * @throws RuntimeException if the price does not exist
     */
    @GetMapping("/{priceId}")
    public ResponseEntity<PriceResponseDTO> get(@PathVariable Long priceId) {
        ProductPrice price = priceService.getPrice(priceId)
                .orElseThrow(() -> new RuntimeException("Price not found with id " + priceId));
        return ResponseEntity.ok(toResponse(price));
    }

    /**
     * Retrieves all active prices for a specific product with pagination.
     *
     * @param productId the product identifier
     * @param page      zero-based page index (default 0)
     * @param size      page size (default 10)
     * @param sort      field to sort by (default "validFrom")
     * @param direction sort direction, either "ASC" or "DESC" (default "ASC")
     * @return {@link PaginatedResponseDTO} of {@link PriceResponseDTO}
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<PaginatedResponseDTO<PriceResponseDTO>> getActive(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "validFrom") String sort,
            @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        PaginatedResponseDTO<ProductPrice> paginated = priceService.getActivePrices(productId, pageable);

        return ResponseEntity.ok(toPaginatedResponse(paginated));
    }

    /**
     * Retrieves the currently active price for a product in a specific currency.
     *
     * @param productId the product identifier
     * @param currency  the {@link Currency} to filter
     * @return {@link PriceResponseDTO} for the active price
     * @throws PriceNotFoundException if no active price exists for the given product and currency
     */
    @GetMapping("/active")
    public ResponseEntity<PriceResponseDTO> getActivePrice(
            @RequestParam Long productId,
            @RequestParam Currency currency) {

        ProductPrice price = priceService.getActivePrice(productId, currency)
                .orElseThrow(() -> new PriceNotFoundException(
                        "No active price found for productId=" + productId + ", currency=" + currency
                ));

        return ResponseEntity.ok(toResponse(price));
    }

    /* =========================
       HELPER METHODS
       ========================= */

    /**
     * Converts a {@link ProductPrice} entity to {@link PriceResponseDTO}.
     *
     * @param price the price entity
     * @return mapped {@link PriceResponseDTO}
     */
    private PriceResponseDTO toResponse(ProductPrice price) {
        return new PriceResponseDTO(
                price.getId(),
                price.getProductId(),
                price.getCurrency().getCode(),
                price.getAmount(),
                price.isActiveNow()
        );
    }

    /**
     * Converts {@link PaginatedResponseDTO} of {@link ProductPrice} to
     * {@link PaginatedResponseDTO} of {@link PriceResponseDTO}.
     * <p>
     * This helps avoid repetitive mapping code in controllers.
     * </p>
     *
     * @param paginated the paginated response containing {@link ProductPrice} entities
     * @return mapped paginated response with {@link PriceResponseDTO}
     */
    private PaginatedResponseDTO<PriceResponseDTO> toPaginatedResponse(PaginatedResponseDTO<ProductPrice> paginated) {
        List<PriceResponseDTO> content = paginated.content().stream()
                .map(this::toResponse)
                .toList();

        return new PaginatedResponseDTO<>(
                content,
                paginated.page(),
                paginated.size(),
                paginated.totalElements(),
                paginated.totalPages(),
                paginated.first(),
                paginated.last()
        );
    }
}
