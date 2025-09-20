package com.chooz.notification.application.dto;


import com.chooz.notification.domain.TargetType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

@QueryProjection
public record NotificationDto(
        Long id,
        Long postId,
        Long receiverId,
        String receiverNickname,
        Long actorId,
        String actorNickname,
        String actorProfileUrl,
        Long targetId,
        TargetType targetType,
        String targetImageUrl,
        boolean isRead,
        LocalDateTime eventAt
) {}
