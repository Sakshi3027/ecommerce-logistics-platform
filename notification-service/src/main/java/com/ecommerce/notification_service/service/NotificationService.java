package com.ecommerce.notification_service.service;

import com.ecommerce.notification_service.dto.NotificationMessage;
import com.ecommerce.notification_service.enums.NotificationEvent;
import com.ecommerce.notification_service.enums.NotificationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotificationService {

    // In-memory store for demo — real system would use DB or email provider
    private final List<NotificationMessage> sentNotifications = new ArrayList<>();

    public void sendOrderNotification(String orderNumber, String status, Long customerId) {
        String subject = getSubjectForStatus(status);
        String message = getMessageForStatus(orderNumber, status);

        NotificationMessage notification = NotificationMessage.builder()
                .recipient("customer-" + customerId + "@ecommerce.com")
                .subject(subject)
                .message(message)
                .type(NotificationType.EMAIL)
                .event(getEventForStatus(status))
                .sentAt(LocalDateTime.now())
                .build();

        sentNotifications.add(notification);
        log.info("📧 EMAIL SENT → To: {} | Subject: {} | Message: {}",
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage());
    }

    public void sendSmsNotification(String phone, String message) {
        NotificationMessage notification = NotificationMessage.builder()
                .recipient(phone)
                .message(message)
                .type(NotificationType.SMS)
                .sentAt(LocalDateTime.now())
                .build();

        sentNotifications.add(notification);
        log.info("📱 SMS SENT → To: {} | Message: {}", phone, message);
    }

    public void sendLowStockAlert(String productName, Integer quantity) {
        NotificationMessage notification = NotificationMessage.builder()
                .recipient("warehouse-team@ecommerce.com")
                .subject("⚠️ Low Stock Alert: " + productName)
                .message("Product '" + productName + "' is running low. Current stock: " + quantity)
                .type(NotificationType.EMAIL)
                .event(NotificationEvent.LOW_STOCK_ALERT)
                .sentAt(LocalDateTime.now())
                .build();

        sentNotifications.add(notification);
        log.info("⚠️ LOW STOCK ALERT → Product: {} | Quantity: {}", productName, quantity);
    }

    public List<NotificationMessage> getAllNotifications() {
        return sentNotifications;
    }

    private String getSubjectForStatus(String status) {
        return switch (status) {
            case "PENDING" -> "✅ Order Placed Successfully";
            case "CONFIRMED" -> "🎉 Order Confirmed";
            case "SHIPPED" -> "🚚 Your Order is on the way";
            case "DELIVERED" -> "📦 Order Delivered";
            case "CANCELLED" -> "❌ Order Cancelled";
            default -> "Order Update";
        };
    }

    private String getMessageForStatus(String orderNumber, String status) {
        return switch (status) {
            case "PENDING" -> "Your order " + orderNumber + " has been placed successfully!";
            case "CONFIRMED" -> "Your order " + orderNumber + " has been confirmed and is being prepared.";
            case "SHIPPED" -> "Your order " + orderNumber + " has been shipped and is on its way!";
            case "DELIVERED" -> "Your order " + orderNumber + " has been delivered. Enjoy!";
            case "CANCELLED" -> "Your order " + orderNumber + " has been cancelled.";
            default -> "Your order " + orderNumber + " status: " + status;
        };
    }

    private NotificationEvent getEventForStatus(String status) {
        return switch (status) {
            case "PENDING" -> NotificationEvent.ORDER_PLACED;
            case "CONFIRMED" -> NotificationEvent.ORDER_CONFIRMED;
            case "SHIPPED" -> NotificationEvent.ORDER_SHIPPED;
            case "DELIVERED" -> NotificationEvent.ORDER_DELIVERED;
            case "CANCELLED" -> NotificationEvent.ORDER_CANCELLED;
            default -> NotificationEvent.ORDER_PLACED;
        };
    }
}