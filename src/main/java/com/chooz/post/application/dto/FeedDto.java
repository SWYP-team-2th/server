package com.chooz.post.application.dto;

import com.chooz.post.domain.Status;
import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

@QueryProjection
public record FeedDto(
        Long postId,
        Status status,
        String title,
        String thumbnailUrl,
        Long postUserId,
        String nickname,
        String profileUrl,
        Long voterCount,
        Long commentCount,
        LocalDateTime createdAt
) {
}