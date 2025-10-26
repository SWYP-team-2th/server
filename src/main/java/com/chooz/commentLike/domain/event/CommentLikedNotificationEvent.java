package com.chooz.commentLike.domain.event;

import java.time.LocalDateTime;

public record CommentLikedNotificationEvent(
        Long commentId,
        Long commentLikeId,
        Long likerId,
        LocalDateTime eventAt
) {}

