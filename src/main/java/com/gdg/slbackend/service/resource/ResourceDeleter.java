package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.domain.resource.ResourceRepository;
import com.gdg.slbackend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResourceDeleter {

    private final ResourceRepository resourceRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void delete(Resource resource) {
        // S3 실제 파일 삭제
        s3Uploader.deleteFile(resource.getImageUrl());

        // DB 삭제
        resourceRepository.delete(resource);
    }
}
