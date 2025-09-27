package com.chooz.notification.application.dto;


import com.chooz.notification.domain.NotificationType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;

@QueryProjection
public record NotificationRowDto(
        Long id,
        NotificationType notificationType,
        String profileUrl,
        String title,
        String content,
        String imageUrl,
        boolean isRead,
        LocalDateTime eventAt
) {}
