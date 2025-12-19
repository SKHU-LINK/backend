package com.gdg.slbackend.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile multipartFile, String directory) {
        try {
            String fileName = directory + "/" +
                    UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            amazonS3Client.putObject(
                    bucket,
                    fileName,
                    multipartFile.getInputStream(),
                    metadata
            );

            return amazonS3Client.getUrl(bucket, fileName).toString();

        } catch (IOException e) {
            throw new GlobalException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * imageUrl 전체를 받아서 내부에서 key로 변환 후 삭제
     */
    public void deleteFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;

        try {
            String key = extractKey(imageUrl);
            amazonS3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new GlobalException(ErrorCode.FILE_DELETE_FAILED);
        }
    }

    /**
     * https://bucket.s3.region.amazonaws.com/posts/xxx.png
     * → posts/xxx.png
     */
    private String extractKey(String imageUrl) {
        int idx = imageUrl.indexOf(".com/");
        if (idx == -1) {
            throw new GlobalException(ErrorCode.INVALID_FILE_URL);
        }
        return imageUrl.substring(idx + 5);
    }
}
