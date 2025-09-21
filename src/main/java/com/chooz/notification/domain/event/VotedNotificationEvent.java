package com.chooz.notification.domain.event;

import java.time.LocalDateTime;

public record VotedNotificationEvent(
        Long postId,
        Long voterId,
        LocalDateTime eventAt
) {}

