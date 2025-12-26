package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.api.comment.dto.CommentRequest;
import com.gdg.slbackend.api.comment.dto.CommentResponse;
import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.post.PostFinder;
import com.gdg.slbackend.service.user.UserFinder;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentCreator commentCreator;
    private final CommentDeleter commentDeleter;
    private final CommentFinder commentFinder;
    private final CommentUpdater commentUpdater;

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
        Comment comment = commentCreator.create(
                postId,
                userFinder.findByIdOrThrow(userId),
                request.getContent()
        );
        return CommentResponse.from(comment);
    }

    /* 댓글 수정 */
    @Transactional
    public CommentResponse updateComment(Long commentId, Long userId, CommentRequest request) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        validateCommentModifyPermission(comment, userId);

        commentUpdater.update(comment, request.getContent());

        return CommentResponse.from(comment);
    }

    /* 댓글 좋아요 */
    @Transactional
    public CommentResponse updateLikes(Long commentId) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        commentUpdater.updateLikes(comment);

        return CommentResponse.from(comment);
    }

    /* 댓글 삭제 */
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentFinder.findByIdOrThrow(commentId);

        validateCommentModifyPermission(comment, userId);

        commentDeleter.delete(comment);
    }

    private void validateCommentModifyPermission(Comment comment, Long userId) {
        boolean isAuthor = comment.getAuthor().getId().equals(userId);
        boolean isSystemAdmin = userFinder.isSystemAdmin(userId);

        if (isAuthor || isSystemAdmin) {
            return;
        }

        Long communityId = postFinder.findByIdOrThrow(comment.getPostId()).getCommunityId();

        // ✅ 인자 순서: (communityId, userId)
        boolean isCommunityAdmin = communityMembershipFinder.findAdminMembershipOrThrow(communityId, userId).getRole().equals(Role.ADMIN);

        if (!isCommunityAdmin) {
            throw new GlobalException(ErrorCode.COMMENT_MODIFY_FORBIDDEN);
        }
    }
}
