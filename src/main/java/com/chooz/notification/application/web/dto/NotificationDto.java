package com.chooz.notification.application.web.dto;


import com.querydsl.core.annotations.QueryProjection;

import java.util.List;

@QueryProjection
public record NotificationDto(
        NotificationRowDto notificationRowDto,
        List<TargetDto> targets
) {}
