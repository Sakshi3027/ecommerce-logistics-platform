package com.ecommerce.delivery_service.dto;

import com.ecommerce.delivery_service.enums.DeliveryStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryResponse {
    private Long id;
    private String deliveryNumber;
    private Long orderId;
    private String orderNumber;
    private Long customerId;
    private DriverResponse driver;
    private DeliveryStatus status;
    private String deliveryAddress;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime actualDeliveryTime;
    private LocalDateTime assignedAt;
    private String notes;
    private LocalDateTime createdAt;
}