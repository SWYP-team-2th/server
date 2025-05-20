package com.chooz.post.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record PollChoiceRequestDto(
        @NotNull
        Long imageFileId
) {
}
