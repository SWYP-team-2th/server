package com.chooz.post.application.dto;

import com.chooz.post.domain.CloseType;

import java.time.LocalDateTime;

public record PostClosedNotificationEvent(
        Long postId,
        Long userId,
        CloseType closeType,
        LocalDateTime eventAt
) {}

