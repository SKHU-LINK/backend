package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.api.resource.dto.ResourceDownloadResponse;
import com.gdg.slbackend.api.resource.dto.ResourceRequest;
import com.gdg.slbackend.api.resource.dto.ResourceResponse;
import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.global.enums.MileageType;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.util.S3Uploader;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.mileage.MileageService;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceFinder resourceFinder;
    private final ResourceCreator resourceCreator;
    private final ResourceUpdater resourceUpdater;
    private final ResourceDeleter resourceDeleter;

    private final CommunityMembershipFinder communityMembershipFinder;
    private final UserFinder userFinder;

    private final MileageService mileageService;

    private final S3Uploader s3Uploader;
    private final S3PresignedUrlService presignedUrlService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

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
        String imageKey = null;

        if (resourceRequest.getMultipartFile() != null &&
                !resourceRequest.getMultipartFile().isEmpty()) {

            // 1️⃣ 업로더는 URL 반환
            String uploadedUrl = s3Uploader.uploadFile(
                    resourceRequest.getMultipartFile(),
                    "resources"
            );

            // 2️⃣ URL → key 추출 + 디코딩
            imageKey = extractKey(uploadedUrl);
        }

        Resource resource = resourceCreator.create(
                communityId,
                userFinder.findByIdOrThrow(userId),
                resourceRequest.getTitle(),
                imageKey // 🔥 key만 저장
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

    public ResourceDownloadResponse getDownloadUrl(Long resourceId, Long downloaderId) {

        Resource resource = resourceFinder.findByIdOrThrow(resourceId);

        // 1️⃣ imageUrl → S3 key 추출 + 디코딩
        String key = extractKey(resource.getImageUrl());

        // 2️⃣ Presigned URL 생성
        String downloadUrl = presignedUrlService.generateDownloadUrl(
                bucket,
                key
        );

        mileageService.change(downloaderId, MileageType.RESOURCE_DOWNLOAD);
        mileageService.change(resource.getUploader().getId(), MileageType.RESOURCE_DOWNLOAD_UPLOADER_REWARD);

        log.info("Before return download response");

        ResourceDownloadResponse resourceDownloadResponse = ResourceDownloadResponse.builder()
                .resourceId(resource.getId())
                .downloadUrl(downloadUrl)
                .build();

        log.info("After build response");

        return resourceDownloadResponse;
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
                communityMembershipFinder.findAdminMembershipOrThrow(resource.getCommunityId(), userId).getRole().equals(Role.ADMIN);
        boolean isSystemAdmin =
                userFinder.isSystemAdmin(userId);

        if (!isUploader && !isCommunityAdmin && !isSystemAdmin) {
            throw new GlobalException(ErrorCode.RESOURCE_MODIFY_FORBIDDEN);
        }
    }

    private String extractKey(String imageUrl) {
        if (imageUrl == null) {
            throw new IllegalArgumentException("imageUrl is null");
        }

        String key;

        if (imageUrl.startsWith("http")) {
            int idx = imageUrl.indexOf(".amazonaws.com/");
            if (idx == -1) {
                throw new IllegalArgumentException("Invalid S3 URL format: " + imageUrl);
            }
            key = imageUrl.substring(idx + ".amazonaws.com/".length());
        } else {
            key = imageUrl;
        }

        // 🔥 핵심: URL 디코딩
        return URLDecoder.decode(key, StandardCharsets.UTF_8);
    }

}
