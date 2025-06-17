package com.chooz.image.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PresignedUrlRequest(
        @NotNull Long contentLength,
        @NotBlank String contentType
) {
}
