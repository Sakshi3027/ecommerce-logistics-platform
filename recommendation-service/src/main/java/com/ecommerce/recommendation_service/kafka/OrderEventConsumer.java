package com.ecommerce.recommendation_service.kafka;

import com.ecommerce.recommendation_service.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final RecommendationService recommendationService;

    @KafkaListener(topics = "order-events", groupId = "recommendation-service-group")
    public void consumeOrderEvent(Map<String, Object> event) {
        String status = (String) event.get("status");
        log.info("Recommendation service received event: {}", status);

        if ("PENDING".equals(status) || "CONFIRMED".equals(status)) {
            try {
                Long customerId = Long.valueOf(event.get("customerId").toString());
                Long productId = Long.valueOf(event.getOrDefault("productId", 0L).toString());
                String productName = (String) event.getOrDefault("productName", "Unknown Product");

                if (productId > 0) {
                    recommendationService.recordPurchase(customerId, productId, productName, null);
                    recommendationService.updateTrending(productId, productName, null);
                    log.info("Recorded purchase for customer {} product {}", customerId, productId);
                }
            } catch (Exception e) {
                log.warn("Could not process recommendation event: {}", e.getMessage());
            }
        }
    }
}