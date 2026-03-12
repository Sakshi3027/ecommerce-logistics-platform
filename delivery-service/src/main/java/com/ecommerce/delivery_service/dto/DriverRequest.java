package com.ecommerce.delivery_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DriverRequest {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Phone is required")
    private String phone;

    @NotNull(message = "Email is required")
    private String email;

    private String vehicleType;
    private String vehiclePlate;
    private String currentCity;
    private Double currentLatitude;
    private Double currentLongitude;
}