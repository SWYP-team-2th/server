package com.chooz.image.presentation.dto;

public record PresignedUrlResponse(
        String signedUploadPutUrl,
        String signedGetUrl,
        String assetUrl
) {
}
