package com.gdg.slbackend.api.comment.dto;

import com.gdg.slbackend.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private String content;
    private Long authorId;

    public static CommentResponse from(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthorId())
                .build();
    }
}
