package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.api.resource.dto.ResourceDownloadResponse;
import com.gdg.slbackend.api.resource.dto.ResourceRequest;
import com.gdg.slbackend.api.resource.dto.ResourceResponse;
import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.domain.user.User;
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

import java.net.URI;
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

    @Transactional
    public ResourceDownloadResponse getDownloadUrl(Long resourceId, Long downloaderId) {

        // 1️⃣ 리소스 조회
        Resource resource = resourceFinder.findByIdOrThrow(resourceId);

        // 2️⃣ imageUrl 검증
        String imageUrl = resource.getImageUrl();
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new GlobalException(ErrorCode.RESOURCE_IMAGE_URL_NOT_FOUND);
        }

        // 3️⃣ S3 key 추출
        String key;
        try {
            key = extractKey(imageUrl);
        } catch (Exception e) {
            log.error("Failed to extract S3 key. imageUrl={}", imageUrl, e);
            throw new GlobalException(ErrorCode.INVALID_RESOURCE_IMAGE_URL);
        }

        // 4️⃣ Presigned URL 생성
        String downloadUrl;
        try {
            downloadUrl = presignedUrlService.generateDownloadUrl(bucket, key);
        } catch (Exception e) {
            log.error("Failed to generate presigned url. bucket={}, key={}", bucket, key, e);
            throw new GlobalException(ErrorCode.PRESIGNED_URL_GENERATION_FAILED);
        }

        // 5️⃣ 업로더 검증
        User uploader = resource.getUploader();
        if (uploader == null || uploader.getId() == null) {
            throw new GlobalException(ErrorCode.RESOURCE_UPLOADER_NOT_FOUND);
        }

        // 6️⃣ 마일리지 처리 (다운로더)
        try {
            mileageService.change(downloaderId, MileageType.RESOURCE_DOWNLOAD);
        } catch (Exception e) {
            log.error("Failed to change mileage for downloader. downloaderId={}", downloaderId, e);
            throw new GlobalException(ErrorCode.MILEAGE_CHANGE_FAILED);
        }

        // 7️⃣ 마일리지 처리 (업로더 보상)
        try {
            mileageService.change(
                    uploader.getId(),
                    MileageType.RESOURCE_DOWNLOAD_UPLOADER_REWARD
            );
        } catch (Exception e) {
            log.error("Failed to reward uploader mileage. uploaderId={}", uploader.getId(), e);
            throw new GlobalException(ErrorCode.MILEAGE_REWARD_FAILED);
        }

        // 8️⃣ 응답 생성
        return ResourceDownloadResponse.builder()
                .resourceId(resource.getId())
                .downloadUrl(downloadUrl)
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

        // 1️⃣ 업로더 여부
        User uploader = resource.getUploader();
        boolean isUploader =
                uploader != null && uploader.getId().equals(userId);

        // 2️⃣ 커뮤니티 관리자 여부 (절대 throw 안 함)
        boolean isCommunityAdmin =
                communityMembershipFinder.isCommunityAdmin(
                        resource.getCommunityId(),
                        userId
                );

        // 3️⃣ 시스템 관리자 여부
        boolean isSystemAdmin =
                userFinder.isSystemAdmin(userId);

        // 4️⃣ 권한 검증
        if (!isUploader && !isCommunityAdmin && !isSystemAdmin) {
            throw new GlobalException(ErrorCode.RESOURCE_MODIFY_FORBIDDEN);
        }
    }


    private String extractKey(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl is null or blank");
        }

        if (!imageUrl.startsWith("http")) {
            return imageUrl; // key는 절대 가공하지 않는다
        }

        URI uri = URI.create(imageUrl);
        return uri.getPath().substring(1); // decode ❌
    }


}
