package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.util.S3Uploader;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    final private PostCreator postCreator;
    final private PostFinder postFinder;
    final private PostUpdater postUpdater;
    final private PostDeleter postDeleter;

    final private UserFinder userFinder;

    final private CommunityMembershipFinder communityMembershipFinder;

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

    public PostResponse getPost(Long communityId, Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);

        postUpdater.updateViews(post);

        return PostResponse.from(post);
    }

    public List<PostResponse> getAllPosts(Long communityId, Long lastId) {
        return postFinder.findAllPost(communityId, lastId)
                .stream()
                .map(PostResponse::from)
                .toList();
    }

    public PostResponse updatePinned(Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        postUpdater.updatePinned(post);

        return PostResponse.from(post);
    }

    public PostResponse updatePost(PostRequest postRequest, Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        String newImageUrl = null;
        MultipartFile file = postRequest.getMultipartFile();
        if (file != null && !file.isEmpty()) {
            newImageUrl = s3Uploader.uploadFile(file, "posts");
        }

        postUpdater.updatePost(postRequest, post, newImageUrl);

        return PostResponse.from(post);
    }

    public PostResponse updateLikes(Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);

        postUpdater.updateLikes(post);

        return PostResponse.from(post);
    }

    public void deletePost(Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        postDeleter.delete(post);
    }

    private void validatePostModifyPermission(Post post, Long userId) {
        boolean isAuthor = post.getAuthorId().equals(userId);
        boolean isCommunityAdmin =
                communityMembershipFinder.isAdmin(userId, post.getCommunityId());
        boolean isSystemAdmin =
                userFinder.isSystemAdmin(userId);

        if (!isAuthor && !isCommunityAdmin && !isSystemAdmin) {
            throw new GlobalException(ErrorCode.POST_MODIFY_FORBIDDEN);
        }
    }
}
