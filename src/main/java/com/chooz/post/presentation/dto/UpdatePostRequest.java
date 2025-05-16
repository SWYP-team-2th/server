package com.chooz.post.presentation.dto;

import jakarta.validation.constraints.NotNull;

public record UpdatePostRequest(
        @NotNull
        String description
) {
}
