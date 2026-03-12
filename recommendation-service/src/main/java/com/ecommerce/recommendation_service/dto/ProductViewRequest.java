package com.ecommerce.recommendation_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductViewRequest {
    @NotNull private Long customerId;
    @NotNull private Long productId;
    @NotNull private String productName;
    private String category;
}