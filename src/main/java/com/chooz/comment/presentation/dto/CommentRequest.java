package com.chooz.comment.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record CommentRequest(
        @NotEmpty
        String content
) {
}
