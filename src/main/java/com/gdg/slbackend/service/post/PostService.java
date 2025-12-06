package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    final private PostCreator postCreator;
    final private PostFinder postFinder;
    final private S3Uploader s3Uploader;

    public PostResponse createPost(PostRequest postRequest, Long userId) {
        String imageUrl = null;

        if (postRequest.getMultipartFile() != null && !postRequest.getMultipartFile().isEmpty()) {
            imageUrl = s3Uploader.uploadFile(postRequest.getMultipartFile(), "posts");
        }

        return PostResponse.from(
                postCreator.createPost(postRequest, userId, imageUrl)
        );
    }

    public Optional<PostResponse> getPinnedPost(Long communityId) {
        return postFinder.findPinnedPost(communityId)
                .map(PostResponse::from);
    }

    public List<PostResponse> getAllPosts(Long communityId, Long lastId) {
        return postFinder.findAllPost(communityId, lastId)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse updatePinned(Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);

        if (!post.isPinned()) {
            postFinder.findPinnedPost(post.getCommunityId())
                    .ifPresent(Post::togglePinned);
        }

        post.togglePinned();
        return PostResponse.from(post);
    }
}
