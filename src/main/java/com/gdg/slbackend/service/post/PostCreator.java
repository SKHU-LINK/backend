package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostRepository;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PostCreator {
    final private PostRepository postRepository;
    final private UserFinder userFinder;

    public Post createPost(PostRequest postRequest, Long authorId, Long communityId, String imageUrl) {
        Post post = Post.builder()
                .category(postRequest.getCategory())
                .title(postRequest.getTitle())
                .content(postRequest.getContent())
                .views(0L)
                .likes(0L)
                .pinned(false)
                .communityId(communityId)
                .authorId(authorId)
                .authorNickname(userFinder.findUserNameByIdOrThrow(authorId))
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return postRepository.save(post);
    }
}
