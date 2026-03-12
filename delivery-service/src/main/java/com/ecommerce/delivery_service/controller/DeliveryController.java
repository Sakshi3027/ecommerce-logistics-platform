package com.ecommerce.delivery_service.controller;

import com.ecommerce.delivery_service.dto.*;
import com.ecommerce.delivery_service.enums.DeliveryStatus;
import com.ecommerce.delivery_service.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    // POST /api/deliveries
    @PostMapping
    public ResponseEntity<DeliveryResponse> createDelivery(@Valid @RequestBody DeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.createDelivery(request));
    }

    // GET /api/deliveries/{id}
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    // GET /api/deliveries/order/{orderId}
    @GetMapping("/order/{orderId}")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(deliveryService.getDeliveryByOrderId(orderId));
    }

    // GET /api/deliveries/pending
    @GetMapping("/pending")
    public ResponseEntity<List<DeliveryResponse>> getPendingDeliveries() {
        return ResponseEntity.ok(deliveryService.getPendingDeliveries());
    }

    // POST /api/deliveries/{id}/auto-assign
    @PostMapping("/{id}/auto-assign")
    public ResponseEntity<DeliveryResponse> autoAssignDriver(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.autoAssignDriver(id));
    }

    // PUT /api/deliveries/{id}/assign/{driverId}
    @PutMapping("/{id}/assign/{driverId}")
    public ResponseEntity<DeliveryResponse> assignDriver(
            @PathVariable Long id, @PathVariable Long driverId) {
        return ResponseEntity.ok(deliveryService.assignDriver(id, driverId));
    }

    // PUT /api/deliveries/{id}/status
    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryResponse> updateStatus(
            @PathVariable Long id, @RequestParam DeliveryStatus status) {
        return ResponseEntity.ok(deliveryService.updateStatus(id, status));
    }

    // ---- Driver endpoints ----
    @PostMapping("/drivers")
    public ResponseEntity<DriverResponse> registerDriver(@Valid @RequestBody DriverRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryService.registerDriver(request));
    }

    @GetMapping("/drivers/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers() {
        return ResponseEntity.ok(deliveryService.getAvailableDrivers());
    }

    @GetMapping("/drivers/{driverId}/deliveries")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByDriver(@PathVariable Long driverId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByDriver(driverId));
    }

    @PutMapping("/drivers/{driverId}/location")
    public ResponseEntity<DriverResponse> updateDriverLocation(
            @PathVariable Long driverId,
            @Valid @RequestBody LocationUpdateRequest request) {
        return ResponseEntity.ok(deliveryService.updateDriverLocation(driverId, request));
    }
}