package com.chooz.image.application;

import com.chooz.image.application.dto.PresignedUrlRequestDto;
import com.chooz.image.presentation.dto.PresignedUrlRequest;

public interface S3Client {
    String getPresignedPutUrl(PresignedUrlRequestDto presignedUrlRequestDto);
}
