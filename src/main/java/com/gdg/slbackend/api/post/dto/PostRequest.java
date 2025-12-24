package com.gdg.slbackend.api.post.dto;

import com.gdg.slbackend.global.enums.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequest {
    @NotBlank
    private String title;

    private String content;

    @NotNull
    private Category category;

    private MultipartFile multipartFile;
}
