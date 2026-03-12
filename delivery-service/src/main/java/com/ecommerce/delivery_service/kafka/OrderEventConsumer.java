package com.ecommerce.delivery_service.kafka;

import com.ecommerce.delivery_service.dto.DeliveryRequest;
import com.ecommerce.delivery_service.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "order-events", groupId = "delivery-service-group")
    public void consumeOrderEvent(Map<String, Object> event) {
        log.info("Delivery service received order event: {}", event.get("status"));

        String status = (String) event.get("status");

        if ("CONFIRMED".equals(status)) {
            log.info("Order confirmed - auto creating delivery for order: {}", event.get("orderNumber"));
            try {
                DeliveryRequest request = new DeliveryRequest();
                request.setOrderId(Long.valueOf(event.get("orderId").toString()));
                request.setOrderNumber((String) event.get("orderNumber"));
                request.setCustomerId(Long.valueOf(event.get("customerId").toString()));
                request.setDeliveryAddress((String) event.get("shippingAddress"));
                deliveryService.createDelivery(request);
            } catch (Exception e) {
                log.error("Failed to auto-create delivery: {}", e.getMessage());
            }
        }
    }
}