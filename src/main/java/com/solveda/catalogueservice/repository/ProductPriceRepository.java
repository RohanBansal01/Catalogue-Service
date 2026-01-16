package com.solveda.catalogueservice.repository;

import com.solveda.catalogueservice.model.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {

    List<ProductPrice> findByProductId(Long productId);

    List<ProductPrice> findByProductIdAndValidToIsNull(Long productId); // active price
}
