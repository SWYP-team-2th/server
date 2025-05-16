package com.chooz.post.presentation.dto;

import com.chooz.post.domain.Status;

public record FeedDto(
        Long postId,
        Status status,
        String description,
        String shareUrl,
        Long postUserId,
        String nickname,
        String profileUrl,
        Long participantCount,
        Long commentCount) {
}