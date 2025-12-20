package com.gdg.slbackend.api.resource.dto;

import com.gdg.slbackend.domain.resource.Resource;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ResourceResponse {

    private Long id;
    private String title;
    private String fileId;
    private Long uploaderId;

    public static ResourceResponse from(Resource resource) {
        return ResourceResponse.builder()
                .id(resource.getId())
                .title(resource.getTitle())
                .fileId(resource.getImageUrl())
                .uploaderId(resource.getUploaderId())
                .build();
    }
}
