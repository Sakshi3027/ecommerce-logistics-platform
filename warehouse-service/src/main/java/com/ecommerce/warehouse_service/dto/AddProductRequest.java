package com.ecommerce.warehouse_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddProductRequest {

    @NotNull
    private Long warehouseId;

    @NotNull
    private Long productId;

    @NotNull
    private String productName;

    @NotNull @Min(1)
    private Integer quantity;

    private String shelfLocation;
}