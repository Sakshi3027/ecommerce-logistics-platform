package com.ecommerce.recommendation_service.service;

import com.ecommerce.recommendation_service.dto.RecommendationResponse;
import com.ecommerce.recommendation_service.entity.*;
import com.ecommerce.recommendation_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final ProductViewRepository productViewRepository;
    private final ProductPurchaseRepository productPurchaseRepository;
    private final TrendingProductRepository trendingProductRepository;

    @Transactional
    public void recordView(Long customerId, Long productId, String productName, String category) {
        var existing = productViewRepository.findByCustomerIdAndProductId(customerId, productId);
        if (existing.isPresent()) {
            ProductView pv = existing.get();
            pv.setViewCount(pv.getViewCount() + 1);
            productViewRepository.save(pv);
        } else {
            productViewRepository.save(ProductView.builder()
                    .customerId(customerId)
                    .productId(productId)
                    .productName(productName)
                    .category(category)
                    .viewCount(1)
                    .build());
        }
        updateTrending(productId, productName, category);
    }

    @Transactional
    public void recordPurchase(Long customerId, Long productId, String productName, String category) {
        var existing = productPurchaseRepository.findByCustomerIdAndProductId(customerId, productId);
        if (existing.isPresent()) {
            ProductPurchase pp = existing.get();
            pp.setPurchaseCount(pp.getPurchaseCount() + 1);
            productPurchaseRepository.save(pp);
        } else {
            productPurchaseRepository.save(ProductPurchase.builder()
                    .customerId(customerId)
                    .productId(productId)
                    .productName(productName)
                    .category(category)
                    .purchaseCount(1)
                    .build());
        }
    }

    @Transactional
    public void updateTrending(Long productId, String productName, String category) {
        var existing = trendingProductRepository.findByProductId(productId);
        if (existing.isPresent()) {
            TrendingProduct tp = existing.get();
            tp.setTotalPurchases(tp.getTotalPurchases() + 1);
            tp.setTotalViews(tp.getTotalViews() + 1);
            trendingProductRepository.save(tp);
        } else {
            trendingProductRepository.save(TrendingProduct.builder()
                    .productId(productId)
                    .productName(productName)
                    .category(category)
                    .totalPurchases(1)
                    .totalViews(1)
                    .trendingScore(0.0)
                    .build());
        }
    }

    // Get personalized recommendations based on purchase history
    public List<RecommendationResponse> getRecommendations(Long customerId) {
        List<ProductPurchase> purchases = productPurchaseRepository
                .findByCustomerIdOrderByPurchaseCountDesc(customerId);

        if (!purchases.isEmpty()) {
            return purchases.stream()
                    .map(p -> RecommendationResponse.builder()
                            .productId(p.getProductId())
                            .productName(p.getProductName())
                            .category(p.getCategory())
                            .score(p.getPurchaseCount() * 10.0)
                            .reason("Based on your purchase history")
                            .build())
                    .collect(Collectors.toList());
        }

        // Fall back to trending if no history
        return getTrendingProducts();
    }

    public List<RecommendationResponse> getTrendingProducts() {
        return trendingProductRepository.findTop10ByOrderByTrendingScoreDesc()
                .stream()
                .map(tp -> RecommendationResponse.builder()
                        .productId(tp.getProductId())
                        .productName(tp.getProductName())
                        .category(tp.getCategory())
                        .score(tp.getTrendingScore())
                        .reason("Trending now")
                        .build())
                .collect(Collectors.toList());
    }

    public List<RecommendationResponse> getSimilarProducts(Long productId) {
        List<ProductView> views = productViewRepository
                .findByProductIdOrderByViewCountDesc(productId);

        return views.stream()
                .map(pv -> RecommendationResponse.builder()
                        .productId(pv.getProductId())
                        .productName(pv.getProductName())
                        .category(pv.getCategory())
                        .score(pv.getViewCount() * 5.0)
                        .reason("Customers also viewed")
                        .build())
                .collect(Collectors.toList());
    }
}