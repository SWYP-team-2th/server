package com.chooz.notification.domain.event;

import java.time.LocalDateTime;

public record CommentLikedEvent(
        Long commentId,
        Long commentLikeId,
        Long likerId,
        LocalDateTime eventAt
) {}

