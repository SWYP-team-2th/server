package com.chooz.image.infrastructure;

import com.chooz.image.application.S3Client;
import com.chooz.image.application.dto.PresignedUrlRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;

@Component
public class AwsS3Client implements S3Client {

    private static final Duration PRESIGNED_URL_EXPIRATION = Duration.ofMinutes(5);

    private final String bucket;
    private final S3Presigner s3Presigner;

    public AwsS3Client(
            @Value("${spring.cloud.aws.s3.bucket}") String bucket,
            S3Presigner s3Presigner
    ) {
        this.bucket = bucket;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String getPresignedPutUrl(PresignedUrlRequestDto presignedUrlRequestDto) {
        PutObjectPresignRequest presignRequest = buildPresignedRequest(presignedUrlRequestDto);
        return s3Presigner.presignPutObject(presignRequest)
                .url()
                .toString();
    }

    private PutObjectPresignRequest buildPresignedRequest(PresignedUrlRequestDto dto) {
        PutObjectRequest.Builder requestBuilder = PutObjectRequest.builder()
                .bucket(bucket)
                .contentType(dto.contentType())
                .contentLength(dto.contentLength())
                .key(dto.assetUrl());
        return PutObjectPresignRequest.builder()
                .signatureDuration(PRESIGNED_URL_EXPIRATION)
                .putObjectRequest(requestBuilder.build())
                .build();
    }
}
