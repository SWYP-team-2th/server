package com.chooz.notification.application.dto;

import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TargetUserDto(
        Long id,
        String nickname,
        String profileUrl
) {}
