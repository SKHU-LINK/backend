package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.api.comment.dto.CommentRequest;
import com.gdg.slbackend.api.comment.dto.CommentResponse;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.post.PostFinder;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentFinder commentFinder;
    private final CommentCreator commentCreator;
    private final CommentUpdater commentUpdater;
    private final CommentDeleter commentDeleter;

    private final UserFinder userFinder;
    private final PostFinder postFinder;
    private final CommunityMembershipFinder communityMembershipFinder;

    /* 댓글 목록 조회 */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId) {
        return commentFinder.findByPostId(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    /* 댓글 작성 */
    public CommentResponse createComment(Long postId, Long userId, CommentRequest request) {
        Comment comment = commentCreator.create(postId, userId, request.getContent());
        return CommentResponse.from(comment);
    }

    /* 댓글 수정 - 작성자만 */
    public CommentResponse updateComment(Long commentId, Long userId, CommentRequest request) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        validateCommentModifyPermission(comment, userId);

        commentUpdater.update(comment, request.getContent());
        return CommentResponse.from(comment);
    }

    public CommentResponse updateLikes(Long commentId) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        commentUpdater.updateLikes(comment);

        return CommentResponse.from(comment);
    }

    /* 댓글 삭제 - 작성자 or 커뮤니티 ADMIN */
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        validateCommentModifyPermission(comment, userId);

        commentDeleter.delete(comment);
    }

    private void validateCommentModifyPermission(Comment comment, Long userId) {
        boolean isAuthor = comment.getAuthorId().equals(userId);
        boolean isSystemAdmin = userFinder.isSystemAdmin(userId);

        if (isAuthor || isSystemAdmin) {
            return;
        }

        Long communityId = postFinder.findByIdOrThrow(comment.getPostId())
                .getCommunityId();

        boolean isCommunityAdmin =
                communityMembershipFinder.isAdmin(userId, communityId);

        if (!isCommunityAdmin) {
            throw new GlobalException(ErrorCode.COMMENT_MODIFY_FORBIDDEN);
        }
    }

}
