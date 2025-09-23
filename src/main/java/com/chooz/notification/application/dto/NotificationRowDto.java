package com.chooz.notification.application.dto;


import com.chooz.notification.domain.NotificationType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;

@QueryProjection
public record NotificationRowDto(
        Long id,
        Long receiverId,
        Long actorId,
        String actorNickname,
        String actorProfileUrl,
        NotificationType notificationType,
        String imageUrl,
        boolean isValid,
        boolean isRead,
        LocalDateTime eventAt
) {}
