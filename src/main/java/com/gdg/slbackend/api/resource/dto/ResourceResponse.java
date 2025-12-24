package com.gdg.slbackend.api.resource.dto;

import com.gdg.slbackend.domain.resource.Resource;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ResourceResponse {

    private Long id;
    private String title;
    private String fileId;
    private Long uploaderId;
    private String uploaderNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ResourceResponse from(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .fileId(resource.getImageUrl())
                .uploaderId(resource.getUploaderId())
                .uploaderNickname(resource.getUploaderNickname())
                .createdAt(resource.getCreatedAt())
                .updatedAt(resource.getUpdatedAt())
                .build();
    }
}
