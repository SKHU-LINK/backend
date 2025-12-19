package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostRepository;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostCreator {
    final private PostRepository postRepository;
    final private UserFinder userFinder;

    public Post createPost(PostRequest postRequest, Long authorId, String imageUrl) {
        return Post.builder()
                .category(postRequest.getCategory())
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .views(0L)
                .pinned(false)
                .communityId(postRequest.getCommunityId())
                .authorId(authorId)
                .authorNickname(userFinder.findUserNameByIdOrThrow(authorId))
                .imageUrl(imageUrl)
                .build();
    }
}
