package com.ecommerce.inventory_service.dto;

import com.ecommerce.inventory_service.enums.InventoryStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InventoryResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantityAvailable;
    private Integer quantityReserved;
    private Integer lowStockThreshold;
    private InventoryStatus status;
    private String warehouseLocation;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}