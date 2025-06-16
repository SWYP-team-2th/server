package com.chooz.image.application;

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
        String path = getAssetUrl();
        String signedGetUrl = getSignedGetUrl(path);
        String presignedUrl = s3Client.getPresignedPutUrl(path, request);
        return new PresignedUrlResponse(presignedUrl, signedGetUrl, path);
    }

    private String getAssetUrl() {
        String imageName = imageNameGenerator.generate();
        return imageProperties.path() + imageName;
    }

    private String getSignedGetUrl(String filePath) {
        URI domain = URI.create(imageProperties.endpoint());
        return domain.resolve(filePath).toString();
    }
}
