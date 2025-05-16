package com.chooz.post.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record PostImageRequestDto(
        @NotNull
        Long imageFileId
) {
}
