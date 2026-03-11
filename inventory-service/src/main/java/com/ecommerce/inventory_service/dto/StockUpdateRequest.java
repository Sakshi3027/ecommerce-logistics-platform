package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull @Min(0)
    private Integer quantity;

    private String reason;
}