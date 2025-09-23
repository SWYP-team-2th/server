package com.chooz.notification.domain.event;

import com.chooz.post.domain.CloseType;

import java.time.LocalDateTime;

public record PostClosedNotificationEvent(
        Long postId,
        CloseType closeType,
        LocalDateTime eventAt
) {}

