package com.chooz.notification.application.web.dto;


import com.chooz.notification.domain.TargetType;
import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TargetDto(
        Long id,
        TargetType type
) {}
