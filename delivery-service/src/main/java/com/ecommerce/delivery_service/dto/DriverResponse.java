package com.ecommerce.delivery_service.dto;

import com.ecommerce.delivery_service.enums.DriverStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverResponse {
    private Long id;
    private String name;
    private String phone;
    private String email;
    private String vehicleType;
    private String vehiclePlate;
    private DriverStatus status;
    private Double currentLatitude;
    private Double currentLongitude;
    private String currentCity;
    private Integer totalDeliveries;
    private Double rating;
}