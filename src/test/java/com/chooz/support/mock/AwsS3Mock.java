package com.chooz.support.mock;

import com.chooz.image.application.S3Client;
import com.chooz.image.application.dto.PresignedUrlRequestDto;

public class AwsS3Mock implements S3Client {

    @Override
    public String getPresignedPutUrl(PresignedUrlRequestDto presignedUrlRequestDto) {
        return "";
    }

    @Override
    public void deleteImage(String assetUrl) {

    }
}
