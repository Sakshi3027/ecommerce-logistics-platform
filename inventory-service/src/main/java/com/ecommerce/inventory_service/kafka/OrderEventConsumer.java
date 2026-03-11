package com.ecommerce.inventory_service.kafka;

import com.ecommerce.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(topics = "order-events", groupId = "inventory-service-group")
    public void consumeOrderEvent(Map<String, Object> event) {
        log.info("Received order event: {}", event);

        String status = (String) event.get("status");
        Long productId = event.get("productId") != null ?
                Long.valueOf(event.get("productId").toString()) : null;

        if ("PENDING".equals(status)) {
            log.info("Order PENDING — inventory check triggered for order: {}",
                    event.get("orderNumber"));
        }

        if ("CANCELLED".equals(status)) {
            log.info("Order CANCELLED — restoring inventory for order: {}",
                    event.get("orderNumber"));
        }
    }
}