package com.chooz.image.application;

import com.chooz.image.application.dto.PresignedUrlRequestDto;

public interface S3Client {
    String getPresignedPutUrl(PresignedUrlRequestDto presignedUrlRequestDto);

    void deleteImage(String assetUrl);
}
