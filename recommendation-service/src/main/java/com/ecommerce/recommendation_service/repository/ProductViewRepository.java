package com.ecommerce.recommendation_service.repository;

import com.ecommerce.recommendation_service.entity.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, Long> {
    List<ProductView> findByCustomerIdOrderByViewCountDesc(Long customerId);
    Optional<ProductView> findByCustomerIdAndProductId(Long customerId, Long productId);
    List<ProductView> findByProductIdOrderByViewCountDesc(Long productId);
}