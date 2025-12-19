package com.gdg.slbackend.api.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CommentRequest {
    @NotBlank
    private String content;
}
