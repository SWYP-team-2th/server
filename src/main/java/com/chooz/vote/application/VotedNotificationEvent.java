package com.chooz.vote.application;

import java.time.LocalDateTime;

public record VotedNotificationEvent(
        Long postId,
        Long voterId,
        LocalDateTime eventAt
) {}

