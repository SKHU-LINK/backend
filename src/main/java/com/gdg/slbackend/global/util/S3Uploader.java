package com.gdg.slbackend.global.util;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
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
            String fileName = directory + "/" + UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            amazonS3Client.putObject(bucket, fileName, multipartFile.getInputStream(), metadata);

            return "https://" + bucket + "/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("S3 upload failed", e);
        }
    }
}
