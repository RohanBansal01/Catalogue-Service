package com.solveda.catalogueservice.service;

import com.solveda.catalogueservice.model.ProductPrice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductPriceService {

    ProductPrice createPrice(Long productId, String currency, BigDecimal amount);

    void changePrice(Long priceId, BigDecimal newAmount);

    void expirePrice(Long priceId);

    Optional<ProductPrice> getPrice(Long priceId);

    List<ProductPrice> getActivePrices(Long productId);
}
