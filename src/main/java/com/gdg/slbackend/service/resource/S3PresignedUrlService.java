package com.gdg.slbackend.service.resource;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class S3PresignedUrlService {

    private final AmazonS3 amazonS3;

    public String generateDownloadUrl(String bucket, String key) {

        // 만료 시간 (5분)
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + 1000 * 60 * 5);

        GeneratePresignedUrlRequest request =
                new GeneratePresignedUrlRequest(bucket, key)
                        .withMethod(com.amazonaws.HttpMethod.GET)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(request);
        return url.toString();
    }
}
