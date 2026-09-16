package com.fighting.goaltracker.domain.notification.dto;

import com.fighting.goaltracker.domain.notification.entity.Notification;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {
    private Integer notificationId;
    private String type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.read = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
}
