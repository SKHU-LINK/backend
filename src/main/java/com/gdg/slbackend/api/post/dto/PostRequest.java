package com.gdg.slbackend.api.post.dto;

import com.gdg.slbackend.global.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class PostRequest {
    @NotBlank
    private String title;

    private String content;

    @NotNull
    private Long communityId;

    @NotNull
    private Category category;

    private MultipartFile multipartFile;
}
