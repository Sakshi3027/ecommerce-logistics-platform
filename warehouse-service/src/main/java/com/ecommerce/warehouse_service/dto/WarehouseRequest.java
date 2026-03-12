package com.ecommerce.warehouse_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WarehouseRequest {

    @NotNull(message = "Warehouse code is required")
    private String warehouseCode;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Location is required")
    private String location;

    private String city;
    private String state;

    @NotNull @Min(1)
    private Integer totalCapacity;
}