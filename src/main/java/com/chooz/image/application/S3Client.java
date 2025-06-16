package com.chooz.image.application;

import com.chooz.image.presentation.dto.PresignedUrlRequest;

public interface S3Client {
    String getPresignedPutUrl(String fileName, PresignedUrlRequest request);
}
