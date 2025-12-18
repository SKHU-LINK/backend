package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.domain.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeleter {
    private final CommentRepository commentRepository;

    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }
}
