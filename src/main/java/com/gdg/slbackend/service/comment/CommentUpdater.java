package com.gdg.slbackend.service.comment;

import com.gdg.slbackend.domain.comment.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentUpdater {
    public void update(Comment comment, String content) {
        comment.updateContent(content);
    }
}
