package com.gdg.slbackend.service.resource;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.gdg.slbackend.api.resource.dto.ResourceDownloadResponse;
import com.gdg.slbackend.api.resource.dto.ResourceRequest;
import com.gdg.slbackend.api.resource.dto.ResourceResponse;
import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.global.enums.MileageType;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.util.S3Uploader;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.mileage.MileageService;
import com.gdg.slbackend.service.user.UserFinder;
import com.gdg.slbackend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceFinder resourceFinder;
    private final ResourceCreator resourceCreator;
    private final ResourceUpdater resourceUpdater;
    private final ResourceDeleter resourceDeleter;

    private final S3Uploader s3Uploader;
    private final AmazonS3 amazonS3Client;

    private final CommunityMembershipFinder communityMembershipFinder;

    private final UserService userService;
    private final UserFinder userFinder;
    private final MileageService mileageService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /* ================= 조회 ================= */

    @Transactional(readOnly = true)
    public List<ResourceResponse> getResources(
            Long communityId,
            Long lastId
    ) {
        return resourceFinder.findAll(communityId, lastId)
                .stream()
                .map(ResourceResponse::from)
                .toList();
    }

    /* ================= 생성 ================= */

    @Transactional
    public ResourceResponse createResource(
            Long communityId,
            Long userId,
            ResourceRequest resourceRequest
    ) {
        String imageUrl = null;

        if (resourceRequest.getMultipartFile() != null && !resourceRequest.getMultipartFile().isEmpty()) {
            imageUrl = s3Uploader.uploadFile(resourceRequest.getMultipartFile(), "resources");
        }

        Resource resource = resourceCreator.create(
                communityId,
                userFinder.findByIdOrThrow(userId),
                resourceRequest.getTitle(),
                imageUrl
        );

        mileageService.change(userId, MileageType.RESOURCE_UPLOAD_REWARD);

        return ResourceResponse.from(resource);
    }

    /* ================= 수정 ================= */

    @Transactional
    public ResourceResponse updateResource(
            Long resourceId,
            Long userId,
            ResourceRequest request
    ) {
        Resource resource = resourceFinder.findByIdOrThrow(resourceId);

        validateModifyPermission(resource, userId);

        resourceUpdater.update(resource, request.getTitle());

        return ResourceResponse.from(resource);
    }

    @Transactional
    public ResourceDownloadResponse downloadResource(Long resourceId, Long downloaderId) {
        // 1. 리소스 조회
        Resource resource = resourceFinder.findByIdOrThrow(resourceId);

        // 2. 마일리지 처리
        mileageService.change(downloaderId, MileageType.RESOURCE_DOWNLOAD);
        mileageService.change(resource.getUploader().getId(), MileageType.RESOURCE_DOWNLOAD_UPLOADER_REWARD);

        Date expiration = new Date();
        long expTime = expiration.getTime();
        expTime += TimeUnit.MINUTES.toMillis(3);
        expiration.setTime(expTime); // 3 Minute

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, resource.getTitle())
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);

        return ResourceDownloadResponse.builder()
                .url(amazonS3Client.generatePresignedUrl(generatePresignedUrlRequest).toString())
                .build();
    }

    /* ================= 삭제 ================= */

    @Transactional
    public void deleteResource(Long resourceId, Long userId) {
        Resource resource = resourceFinder.findByIdOrThrow(resourceId);

        validateModifyPermission(resource, userId);

        resourceDeleter.delete(resource);
    }

    /* ================= 권한 검증 ================= */

    private void validateModifyPermission(Resource resource, Long userId) {
        boolean isUploader = resource.getUploader().getId().equals(userId);
        boolean isCommunityAdmin =
                communityMembershipFinder.isAdmin(userId, resource.getCommunityId());
        boolean isSystemAdmin =
                userFinder.isSystemAdmin(userId);

        if (!isUploader && !isCommunityAdmin && !isSystemAdmin) {
            throw new GlobalException(ErrorCode.RESOURCE_MODIFY_FORBIDDEN);
        }
    }
}
