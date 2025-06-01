package com.chooz.post.presentation.dto;

import com.chooz.post.domain.Status;

public record FeedDto(
        Long postId,
        Status status,
        String title,
        String thumbnailUrl,
        Long postUserId,
        String nickname,
        String profileUrl,
        Long voterCount,
        Long commentCount
) {
}