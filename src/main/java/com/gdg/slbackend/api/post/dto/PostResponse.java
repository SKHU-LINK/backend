package com.gdg.slbackend.api.post.dto;

import com.gdg.slbackend.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private Long views;
    private boolean pinned;

    private Long authorId;
    private String authorNickname;
    private Long communityId;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .views(post.getViews())
                .pinned(post.isPinned())
                .authorId(post.getAuthorId())
                .authorNickname(post.getAuthorNickname())
                .communityId(post.getCommunityId())
                .build();
    }
}
