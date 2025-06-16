package com.chooz.image.application;

import com.chooz.image.presentation.dto.PresignedUrlResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileService {

    private final S3Client s3Client;
    private final ImageNameGenerator imageNameGenerator;

    public PresignedUrlResponse getPresignedUrl() {
        String path = createPath();
        return s3Client.getPresignedUrl(path);
    }

    private String createPath() {
        String fileId = imageNameGenerator.generate();
        return String.format("%s/%s", "image", fileId);
    }
}
