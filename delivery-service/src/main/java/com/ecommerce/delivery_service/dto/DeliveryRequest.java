package com.ecommerce.delivery_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeliveryRequest {

    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Order number is required")
    private String orderNumber;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Delivery address is required")
    private String deliveryAddress;

    private Double destinationLatitude;
    private Double destinationLongitude;
    private String notes;
}