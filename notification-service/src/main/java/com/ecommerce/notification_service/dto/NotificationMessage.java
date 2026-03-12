package com.ecommerce.notification_service.dto;

import com.ecommerce.notification_service.enums.NotificationEvent;
import com.ecommerce.notification_service.enums.NotificationType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class NotificationMessage {
    private String recipient;
    private String subject;
    private String message;
    private NotificationType type;
    private NotificationEvent event;
    private LocalDateTime sentAt;
}