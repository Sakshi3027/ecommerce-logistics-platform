package com.ecommerce.notification_service.controller;

import com.ecommerce.notification_service.dto.NotificationMessage;
import com.ecommerce.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // GET /api/notifications
    @GetMapping
    public ResponseEntity<List<NotificationMessage>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    // POST /api/notifications/low-stock
    @PostMapping("/low-stock")
    public ResponseEntity<String> sendLowStockAlert(
            @RequestParam String productName,
            @RequestParam Integer quantity) {
        notificationService.sendLowStockAlert(productName, quantity);
        return ResponseEntity.ok("Low stock alert sent for: " + productName);
    }
}