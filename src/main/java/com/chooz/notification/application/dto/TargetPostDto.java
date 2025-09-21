package com.chooz.notification.application.dto;


import com.querydsl.core.annotations.QueryProjection;

@QueryProjection
public record TargetPostDto(
        Long id,
        String imageUrl
) {}
