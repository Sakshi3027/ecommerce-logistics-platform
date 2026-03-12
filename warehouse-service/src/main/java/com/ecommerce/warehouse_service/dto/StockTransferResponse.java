package com.ecommerce.warehouse_service.dto;

import com.ecommerce.warehouse_service.enums.StockTransferStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class StockTransferResponse {
    private Long id;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private StockTransferStatus status;
    private String notes;
    private LocalDateTime createdAt;
}