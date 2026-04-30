package com.springnotify.model;

import lombok.Data;

@Data
public class NotificationRequest {
    private String idempotencyKey;
    private String recipient;
    private NotificationType type;
    private String payload;
}
