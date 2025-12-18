package com.gdg.slbackend.api.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResourceRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String fileId;
}

