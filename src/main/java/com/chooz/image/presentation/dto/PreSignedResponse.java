package com.chooz.image.presentation.dto;

public record PreSignedResponse(
        String preSignedUrl,
        String fileName
) {}
