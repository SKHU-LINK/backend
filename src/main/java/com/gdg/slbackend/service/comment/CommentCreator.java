package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.domain.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreator {

    private final CommentRepository commentRepository;

    public Comment create(Long postId, Long userId, String content) {
        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(userId)
                .content(content)
                .build();

        return commentRepository.save(comment);
    }
}

