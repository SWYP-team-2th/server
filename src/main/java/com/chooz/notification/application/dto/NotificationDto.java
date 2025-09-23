package com.chooz.notification.application.dto;


import com.chooz.notification.domain.NotificationType;
import com.chooz.notification.domain.TargetType;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.List;

@QueryProjection
public record NotificationDto(
        NotificationRowDto notificationRowDto,
        List<TargetDto> targets
) {}
