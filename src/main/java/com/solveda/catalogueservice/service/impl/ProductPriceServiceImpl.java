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

@Service
@RequiredArgsConstructor
@Transactional
public class ProductPriceServiceImpl implements ProductPriceService {

    private final ProductPriceRepository priceRepository;

    /* =========================
       CREATE
       ========================= */
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

    @Override
    public void expirePrice(Long priceId) {
        ProductPrice price = findPriceOrThrow(priceId);
        price.expire();
        savePrice(price, "expiring price");
    }

    /* =========================
       READ
       ========================= */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProductPrice> getPrice(Long priceId) {
        return priceRepository.findById(priceId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductPrice> getActivePrices(Long productId) {
        try {
            return priceRepository.findByProductIdAndValidToIsNull(productId);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error fetching active prices for product ID " + productId, ex);
        }
    }

    /* =========================
       INTERNAL HELPERS
       ========================= */
    private ProductPrice findPriceOrThrow(Long priceId) {
        return priceRepository.findById(priceId)
                .orElseThrow(() -> new ProductNotFoundException("Price with ID " + priceId + " not found"));
    }

    private ProductPrice savePrice(ProductPrice price, String operation) {
        try {
            return priceRepository.save(price);
        } catch (DataAccessException ex) {
            throw new DatabaseOperationException("Error while " + operation + " for price ID " + price.getId(), ex);
        }
    }
}
