package com.chooz.image.application.dto;

public record PresignedUrlRequestDto(
        String contentType,
        Long contentLength,
        String assetUrl
) {
}
