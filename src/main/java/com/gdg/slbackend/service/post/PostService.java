package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostLike;
import com.gdg.slbackend.domain.post.PostLikeRepository;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.util.S3Uploader;
import com.gdg.slbackend.service.comment.CommentFinder;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipCreator;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.user.UserFinder;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostCreator postCreator;
    private final PostFinder postFinder;
    private final PostUpdater postUpdater;
    private final PostDeleter postDeleter;

    private final UserFinder userFinder;
    private final CommentFinder commentFinder;
    private final CommunityMembershipCreator communityMembershipCreator;
    private final CommunityMembershipFinder communityMembershipFinder;

    private final PostLikeRepository postLikeRepository;

    private final S3Uploader s3Uploader;

    public PostResponse createPost(PostRequest postRequest, Long communityId, Long userId) {
        validatePostCreatePermission(communityId, userId);

        String imageUrl = uploadPostImageIfExists(postRequest.getMultipartFile());

        Post post = postCreator.createPost(postRequest, userId, communityId, imageUrl);

        return PostResponse.from(post, commentFinder.countByPostId(post.getId()));
    }

    public Optional<PostResponse> getPinnedPost(Long communityId) {
        return postFinder.findPinnedPost(communityId)
                .map(post -> PostResponse.from(post, commentFinder.countByPostId(post.getId())));
    }

    @Transactional
    public PostResponse getPost(Long communityId, Long postId) {
        Post post = postFinder.findByIdOrThrow(postId);

        // TODO: communityId 검증이 필요하면 여기서 post.getCommunityId()와 비교해서 막기
        postUpdater.updateViews(post);

        return PostResponse.from(post, commentFinder.countByPostId(postId));
    }

    public List<PostResponse> getAllPosts(Long communityId, Long lastId, Long userId) {
        CommunityMembership communityMembership = communityMembershipFinder.findByIdOrThrow(communityId, userId);
        if(communityMembership == null) {
            communityMembershipCreator.createCommunityMembershipByCommunityId(communityId, userId, Role.MEMBER, false);
        }

        return postFinder.findAllPost(communityId, lastId)
                .stream()
                .map(post -> PostResponse.from(post, commentFinder.countByPostId(post.getId())))
                .toList();
    }

    @Transactional
    public PostResponse updatePinned(Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        postUpdater.updatePinned(post);

        return PostResponse.from(post, commentFinder.countByPostId(post.getId()));
    }

    public PostResponse updatePost(PostRequest postRequest, Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        String newImageUrl = uploadPostImageIfExists(postRequest.getMultipartFile());

        postUpdater.updatePost(postRequest, post, newImageUrl);

        return PostResponse.from(post, commentFinder.countByPostId(post.getId()));
    }

    public PostResponse toggleLike(Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        Optional<PostLike> like =
                postLikeRepository.findByUserIdAndPost(userId, post);

        if (like.isPresent()) {
            // 좋아요 취소
            postLikeRepository.delete(like.get());
            post.decreaseLikes();
        } else {
            // 좋아요
            postLikeRepository.save(new PostLike(userId, post));
            post.increaseLikes();
        }

        return PostResponse.from(post);
    }


    public void deletePost(Long postId, Long userId) {
        Post post = postFinder.findByIdOrThrow(postId);

        validatePostModifyPermission(post, userId);

        postDeleter.delete(post);
    }

    private void validatePostCreatePermission(Long communityId, Long userId) {
        boolean isMember = communityMembershipFinder.findById(communityId, userId).isPresent();
        if (!isMember) {
            throw new GlobalException(ErrorCode.NOT_COMMUNITY_MEMBER);
        }
    }

    private void validatePostModifyPermission(Post post, Long userId) {
        boolean isAuthor = post.getAuthorId().equals(userId);
        boolean isCommunityAdmin = communityMembershipFinder.isCommunityAdmin(post.getCommunityId(), userId);
        boolean isSystemAdmin = userFinder.isSystemAdmin(userId);

        if (!isAuthor && !isCommunityAdmin && !isSystemAdmin) {
            throw new GlobalException(ErrorCode.POST_MODIFY_FORBIDDEN);
        }
    }

    private String uploadPostImageIfExists(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        return s3Uploader.uploadFile(file, "posts");
    }
}
