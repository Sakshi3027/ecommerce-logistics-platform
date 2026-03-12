package com.ecommerce.recommendation_service.controller;

import com.ecommerce.recommendation_service.dto.RecommendationResponse;
import com.ecommerce.recommendation_service.dto.ProductViewRequest;
import com.ecommerce.recommendation_service.service.RecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    // GET /api/recommendations/{customerId}
    @GetMapping("/{customerId}")
    public ResponseEntity<List<RecommendationResponse>> getRecommendations(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(recommendationService.getRecommendations(customerId));
    }

    // GET /api/recommendations/trending
    @GetMapping("/trending")
    public ResponseEntity<List<RecommendationResponse>> getTrending() {
        return ResponseEntity.ok(recommendationService.getTrendingProducts());
    }

    // GET /api/recommendations/similar/{productId}
    @GetMapping("/similar/{productId}")
    public ResponseEntity<List<RecommendationResponse>> getSimilar(
            @PathVariable Long productId) {
        return ResponseEntity.ok(recommendationService.getSimilarProducts(productId));
    }

    // POST /api/recommendations/view
    @PostMapping("/view")
    public ResponseEntity<String> recordView(@Valid @RequestBody ProductViewRequest request) {
        recommendationService.recordView(
                request.getCustomerId(),
                request.getProductId(),
                request.getProductName(),
                request.getCategory());
        return ResponseEntity.ok("View recorded for product: " + request.getProductName());
    }
}