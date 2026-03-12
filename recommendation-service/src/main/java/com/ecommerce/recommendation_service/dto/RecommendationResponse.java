package com.ecommerce.recommendation_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendationResponse {
    private Long productId;
    private String productName;
    private String category;
    private Double score;
    private String reason;
}