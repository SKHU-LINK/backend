package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentUpdater {
    public void update(Comment comment, String content) {
        comment.updateContent(content);
        comment.updateUpdatedAt(LocalDateTime.now());
    }

    public void updateLikes(Comment comment) {
        comment.increaseLikes();
    }
}
