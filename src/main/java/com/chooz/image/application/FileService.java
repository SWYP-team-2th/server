package com.chooz.image.application;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.chooz.common.util.TimeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class FileService {

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.path}")
    private String path;

    private final AmazonS3 amazonS3;
    private final TimeHelper timeHelper;
    private final ImageNameGenerator imageNameGenerator;

    public String getPreSignedUrl(String fileName) {
        String fileNameWithPath = createPath(fileName);
        GeneratePresignedUrlRequest generatePresignedUrlRequest = getGeneratePreSignedUrlRequest(bucket, fileNameWithPath);
        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    private GeneratePresignedUrlRequest getGeneratePreSignedUrlRequest(String bucket, String fileName) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, fileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(getPreSignedUrlExpiration());
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .build();
        PutObjectPresignRequest build = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(2))
                .putObjectRequest(putObjectRequest)
                .build();
        generatePresignedUrlRequest.addRequestParameter(
                Headers.S3_CANNED_ACL,
                CannedAccessControlList.BucketOwnerFullControl.toString()
        );
        return generatePresignedUrlRequest;
    }

    private Date getPreSignedUrlExpiration() {
        long nowMillis = timeHelper.nowMillis();
        long expiredTimeMillis = nowMillis + TWO_MINUTES;
        return new Date(expiredTimeMillis);
    }

    private String createPath(String fileName) {
        String fileId = imageNameGenerator.generate();
        return String.format("%s/%s", path, fileId + fileName);
    }
}
