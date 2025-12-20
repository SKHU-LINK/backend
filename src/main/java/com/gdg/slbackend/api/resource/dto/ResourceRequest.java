package com.gdg.slbackend.api.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ResourceRequest {
    @NotBlank(message = "제목은 필수입니다")
    private String title;

    @NotNull(message = "파일은 필수입니다")
    private MultipartFile multipartFile;
}

