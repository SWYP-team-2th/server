package com.chooz.post.presentation.dto;

import com.chooz.post.domain.CloseType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CloseOptionDto(
        @NotNull
        CloseType closeType,

        LocalDateTime closedAt,

        Integer maxVoterCount
) {
}
