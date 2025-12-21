package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.domain.comment.CommentRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentFinder {

    private final CommentRepository commentRepository;

    public Comment findByIdOrThrow(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMENT_NOT_FOUND));
    }

    public List<Comment> findByPostId(Long postId) {
        return commentRepository.findByPostIdOrderByIdAsc(postId);
    }

    public long countByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }
}
