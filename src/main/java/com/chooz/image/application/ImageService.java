package com.chooz.image.application;

import com.chooz.image.application.dto.PresignedUrlRequestDto;
import com.chooz.image.presentation.dto.PresignedUrlRequest;
import com.chooz.image.presentation.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;
    private final ImageNameGenerator imageNameGenerator;
    private final ImageProperties imageProperties;
    private final ImageValidator imageValidator;

    public PresignedUrlResponse getPresignedUrl(PresignedUrlRequest request) {
        imageValidator.validate(request.contentType());
        String assetUrl = getAssetUrl();
        String signedGetUrl = getSignedGetUrl(assetUrl);
        String presignedUrl = s3Client.getPresignedPutUrl(
                new PresignedUrlRequestDto(request.contentType(), request.contentLength(), assetUrl)
        );
        return new PresignedUrlResponse(presignedUrl, signedGetUrl, assetUrl);
    }

    private String getAssetUrl() {
        String imageName = imageNameGenerator.generate();
        return imageProperties.path() + imageName;
    }

    private String getSignedGetUrl(String filePath) {
        URI domain = URI.create(imageProperties.endpoint());
        return domain.resolve(filePath).toString();
    }

    public void deleteImage(String assetUrl) {
        s3Client.deleteImage(assetUrl);
    }
}
