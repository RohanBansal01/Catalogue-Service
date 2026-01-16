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

@RestController
@RequestMapping("/prices")
@RequiredArgsConstructor
public class PriceController {

    private final ProductPriceService priceService;

    @PostMapping
    public ResponseEntity<PriceResponseDTO> create(
            @Valid @RequestBody PriceRequestDTO request) {

        ProductPrice price =
                priceService.createPrice(
                        request.productId(),
                        request.currency(),
                        request.amount()
                );

        return ResponseEntity.ok(toResponse(price));
    }

    @PostMapping("/{priceId}/change")
    public ResponseEntity<Void> change(
            @PathVariable Long priceId,
            @RequestParam String amount) {

        priceService.changePrice(priceId, new java.math.BigDecimal(amount));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{priceId}/expire")
    public ResponseEntity<Void> expire(@PathVariable Long priceId) {
        priceService.expirePrice(priceId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{priceId}")
    public ResponseEntity<PriceResponseDTO> get(@PathVariable Long priceId) {
        ProductPrice price = priceService.getPrice(priceId).orElseThrow();
        return ResponseEntity.ok(toResponse(price));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<PriceResponseDTO>> getActive(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                priceService.getActivePrices(productId)
                        .stream()
                        .map(this::toResponse)
                        .toList()
        );
    }

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
