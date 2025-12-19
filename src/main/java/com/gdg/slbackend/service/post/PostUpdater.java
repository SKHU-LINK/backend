package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostRepository;
import com.gdg.slbackend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostUpdater {
    private final PostFinder postFinder;
    private final S3Uploader s3Uploader;

    public void updatePinned(Post post) {
        if (!post.isPinned()) {
            unpinExistingPost(post.getCommunityId());
        }

        post.togglePinned();
    }

    private void unpinExistingPost(Long communityId) {
        postFinder.findPinnedPost(communityId)
                .ifPresent(Post::togglePinned);
    }

    @Transactional
    public void updatePost(PostRequest request, Post post, String newImageUrl) {

        if (newImageUrl != null) {
            // 기존 파일 삭제
            s3Uploader.deleteFile(post.getImageUrl());
            post.updateImageUrl(newImageUrl);
        }

        post.updateTitle(request.getTitle());
        post.updateContent(request.getContent());
        post.updateCategory(request.getCategory());
    }

    public void updateViews(Post post) {
        post.increaseViews();
    }

    public void updateLikes(Post post) {
        post.increaseLikes();
    }
}
