package com.ecommerce.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Product name is required")
    private String productName;

    @NotNull @Min(0)
    private Integer quantityAvailable;

    @NotNull @Min(0)
    private Integer lowStockThreshold;

    private String warehouseLocation;
}