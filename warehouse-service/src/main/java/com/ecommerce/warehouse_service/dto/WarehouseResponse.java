package com.ecommerce.warehouse_service.dto;

import com.ecommerce.warehouse_service.enums.WarehouseStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class WarehouseResponse {
    private Long id;
    private String warehouseCode;
    private String name;
    private String location;
    private String city;
    private String state;
    private Integer totalCapacity;
    private Integer usedCapacity;
    private Integer availableCapacity;
    private WarehouseStatus status;
    private LocalDateTime createdAt;
}