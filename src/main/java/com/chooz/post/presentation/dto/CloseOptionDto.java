package com.chooz.post.presentation.dto;

import com.chooz.post.domain.CloseType;

import java.time.LocalDateTime;

public record CloseOptionDto(
        CloseType closeType,

        LocalDateTime closedAt,

        Integer maxVoterCount
) {
}
