package com.ecommerce.notification_service.kafka;

import com.ecommerce.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;

    @KafkaListener(topics = "order-events", groupId = "notification-service-group")
    public void consumeOrderEvent(Map<String, Object> event) {
        log.info("🔔 Notification service received event: {}", event.get("status"));

        String status = (String) event.get("status");
        String orderNumber = (String) event.get("orderNumber");
        Long customerId = event.get("customerId") != null ?
                Long.valueOf(event.get("customerId").toString()) : null;

        if (orderNumber != null && customerId != null) {
            notificationService.sendOrderNotification(orderNumber, status, customerId);

            // Also send SMS for key events
            if ("SHIPPED".equals(status) || "DELIVERED".equals(status)) {
                notificationService.sendSmsNotification(
                        "+1-555-000-" + customerId,
                        "Your order " + orderNumber + " is " + status.toLowerCase() + "!"
                );
            }
        }
    }
}