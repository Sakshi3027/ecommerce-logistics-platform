package com.ecommerce.recommendation_service.repository;

import com.ecommerce.recommendation_service.entity.ProductPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPurchaseRepository extends JpaRepository<ProductPurchase, Long> {
    List<ProductPurchase> findByCustomerIdOrderByPurchaseCountDesc(Long customerId);
    Optional<ProductPurchase> findByCustomerIdAndProductId(Long customerId, Long productId);
    List<ProductPurchase> findTop10ByOrderByPurchaseCountDesc();
}