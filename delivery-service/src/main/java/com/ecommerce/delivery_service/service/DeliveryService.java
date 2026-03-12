package com.ecommerce.delivery_service.service;

import com.ecommerce.delivery_service.dto.*;
import com.ecommerce.delivery_service.entity.Delivery;
import com.ecommerce.delivery_service.entity.Driver;
import com.ecommerce.delivery_service.enums.DeliveryStatus;
import com.ecommerce.delivery_service.enums.DriverStatus;
import com.ecommerce.delivery_service.repository.DeliveryRepository;
import com.ecommerce.delivery_service.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DriverRepository driverRepository;

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        log.info("Creating delivery for order: {}", request.getOrderNumber());

        Delivery delivery = Delivery.builder()
                .deliveryNumber("DEL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .customerId(request.getCustomerId())
                .deliveryAddress(request.getDeliveryAddress())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .status(DeliveryStatus.PENDING)
                .notes(request.getNotes())
                .estimatedDeliveryTime(LocalDateTime.now().plusHours(24))
                .build();

        return mapToResponse(deliveryRepository.save(delivery));
    }

    @Transactional
    public DeliveryResponse assignDriver(Long deliveryId, Long driverId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));

        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available: " + driverId);
        }

        delivery.setDriver(driver);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());

        driver.setStatus(DriverStatus.ON_DELIVERY);
        driverRepository.save(driver);

        log.info("Driver {} assigned to delivery {}", driver.getName(), delivery.getDeliveryNumber());
        return mapToResponse(deliveryRepository.save(delivery));
    }

    // Auto assign nearest available driver
    @Transactional
    public DeliveryResponse autoAssignDriver(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        List<Driver> availableDrivers = driverRepository.findByStatus(DriverStatus.AVAILABLE);
        if (availableDrivers.isEmpty()) {
            throw new RuntimeException("No available drivers at the moment");
        }

        // Pick driver with highest rating (real system would use location proximity)
        Driver bestDriver = availableDrivers.stream()
                .max((d1, d2) -> Double.compare(d1.getRating(), d2.getRating()))
                .orElseThrow();

        return assignDriver(deliveryId, bestDriver.getId());
    }

    @Transactional
    public DeliveryResponse updateStatus(Long deliveryId, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + deliveryId));

        log.info("Updating delivery {} status to {}", delivery.getDeliveryNumber(), newStatus);
        delivery.setStatus(newStatus);

        if (newStatus == DeliveryStatus.PICKED_UP) {
            delivery.setPickedUpAt(LocalDateTime.now());
        }

        if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setActualDeliveryTime(LocalDateTime.now());
            // Free up the driver
            if (delivery.getDriver() != null) {
                Driver driver = delivery.getDriver();
                driver.setStatus(DriverStatus.AVAILABLE);
                driver.setTotalDeliveries(driver.getTotalDeliveries() + 1);
                driverRepository.save(driver);
            }
        }

        return mapToResponse(deliveryRepository.save(delivery));
    }

    public DeliveryResponse getDeliveryById(Long id) {
        return mapToResponse(deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id)));
    }

    public DeliveryResponse getDeliveryByOrderId(Long orderId) {
        return mapToResponse(deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order: " + orderId)));
    }

    public List<DeliveryResponse> getDeliveriesByDriver(Long driverId) {
        return deliveryRepository.findByDriverId(driverId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<DeliveryResponse> getPendingDeliveries() {
        return deliveryRepository.findByStatus(DeliveryStatus.PENDING)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<DriverResponse> getAvailableDrivers() {
        return driverRepository.findByStatus(DriverStatus.AVAILABLE)
                .stream().map(this::mapDriverToResponse).collect(Collectors.toList());
    }

    @Transactional
    public DriverResponse registerDriver(DriverRequest request) {
        Driver driver = Driver.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .vehicleType(request.getVehicleType())
                .vehiclePlate(request.getVehiclePlate())
                .currentCity(request.getCurrentCity())
                .currentLatitude(request.getCurrentLatitude())
                .currentLongitude(request.getCurrentLongitude())
                .status(DriverStatus.AVAILABLE)
                .build();
        return mapDriverToResponse(driverRepository.save(driver));
    }

    @Transactional
    public DriverResponse updateDriverLocation(Long driverId, LocationUpdateRequest request) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found: " + driverId));
        driver.setCurrentLatitude(request.getLatitude());
        driver.setCurrentLongitude(request.getLongitude());
        if (request.getCity() != null) driver.setCurrentCity(request.getCity());
        return mapDriverToResponse(driverRepository.save(driver));
    }

    private DeliveryResponse mapToResponse(Delivery d) {
        return DeliveryResponse.builder()
                .id(d.getId())
                .deliveryNumber(d.getDeliveryNumber())
                .orderId(d.getOrderId())
                .orderNumber(d.getOrderNumber())
                .customerId(d.getCustomerId())
                .driver(d.getDriver() != null ? mapDriverToResponse(d.getDriver()) : null)
                .status(d.getStatus())
                .deliveryAddress(d.getDeliveryAddress())
                .estimatedDeliveryTime(d.getEstimatedDeliveryTime())
                .actualDeliveryTime(d.getActualDeliveryTime())
                .assignedAt(d.getAssignedAt())
                .notes(d.getNotes())
                .createdAt(d.getCreatedAt())
                .build();
    }

    private DriverResponse mapDriverToResponse(Driver d) {
        return DriverResponse.builder()
                .id(d.getId())
                .name(d.getName())
                .phone(d.getPhone())
                .email(d.getEmail())
                .vehicleType(d.getVehicleType())
                .vehiclePlate(d.getVehiclePlate())
                .status(d.getStatus())
                .currentLatitude(d.getCurrentLatitude())
                .currentLongitude(d.getCurrentLongitude())
                .currentCity(d.getCurrentCity())
                .totalDeliveries(d.getTotalDeliveries())
                .rating(d.getRating())
                .build();
    }
}