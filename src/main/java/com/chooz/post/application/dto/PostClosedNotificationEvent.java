package com.chooz.post.application.dto;

import java.time.LocalDateTime;

public record PostClosedNotificationEvent(
        Long postId,
        Long receiverId,
        LocalDateTime eventAt
) {}

