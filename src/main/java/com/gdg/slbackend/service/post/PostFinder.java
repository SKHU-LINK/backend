package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostRepository;
import com.gdg.slbackend.global.enums.Category;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PostFinder {
    final private PostRepository postRepository;

    @Transactional(readOnly = true)
    public Post findByIdOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Post findByTitleOrThrow(String title) {
        return postRepository.findByTitle(title)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_INVALID_TITLE));
    }

    @Transactional(readOnly = true)
    public Post findByAuthorNicknameOrThrow(String authorNickname) {
        return postRepository.findByAuthorNickname(authorNickname)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_INVALID_AUTHOR_NICKNAME));
    }

    @Transactional(readOnly = true)
    public Post findByCategoryOrThrow(Category category) {
        return postRepository.findByCategory(category)
                .orElseThrow(() -> new GlobalException(ErrorCode.POST_INVALID_CATEGORY));
    }

    @Transactional(readOnly = true)
    public Optional<Post> findPinnedPost(Long communityId) {
        return postRepository.findByCommunityIdAndPinnedTrue(communityId);
    }

    @Transactional(readOnly = true)
    public List<Post> findAllPost(Long communityId, Long lastId) {
        Pageable pageable = PageRequest.of(0, 10);
        return postRepository.findNextPosts(communityId, lastId, pageable);
    }
}
