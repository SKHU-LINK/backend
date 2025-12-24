package com.gdg.slbackend.api.resource.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResourceDownloadResponse {
    private Long resourceId;
    private String downloadUrl;
}
