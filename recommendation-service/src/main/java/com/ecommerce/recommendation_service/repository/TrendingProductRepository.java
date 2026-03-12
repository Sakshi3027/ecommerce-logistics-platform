package com.ecommerce.recommendation_service.repository;

import com.ecommerce.recommendation_service.entity.TrendingProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TrendingProductRepository extends JpaRepository<TrendingProduct, Long> {
    List<TrendingProduct> findTop10ByOrderByTrendingScoreDesc();
    Optional<TrendingProduct> findByProductId(Long productId);
    List<TrendingProduct> findByCategoryOrderByTrendingScoreDesc(String category);
}