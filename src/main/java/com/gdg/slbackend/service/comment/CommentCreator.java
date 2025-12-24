package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import com.gdg.slbackend.domain.comment.CommentRepository;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentCreator {

    private final CommentRepository commentRepository;
    private final UserFinder userFinder;

    @Transactional
    public Comment create(Long postId, User author, String content) {
        User user = userFinder.findByIdOrThrow(author.getId());

        Comment comment = Comment.builder()
                .content(content)
                .postId(postId)
                .author(author)
                .likes(0L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        commentRepository.save(comment);
        return commentRepository.save(comment);
    }
}
