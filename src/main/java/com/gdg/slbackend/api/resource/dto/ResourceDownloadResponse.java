package com.gdg.slbackend.api.resource.dto;

import com.gdg.slbackend.domain.resource.Resource;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
@Schema(description = "AWS S3 URL 응답 정보")
@Setter
@Builder
public class ResourceDownloadResponse {
    private String url;
}
