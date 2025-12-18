package com.gdg.slbackend.api.comment;

import com.gdg.slbackend.api.comment.dto.CommentRequest;
import com.gdg.slbackend.api.comment.dto.CommentResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.comment.CommentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping
@Tag(name = "Comment")
public class CommentController {

    private final CommentService commentService;

    /* 댓글 목록 */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(commentService.getComments(postId));
    }

    /* 댓글 작성 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(
            @PathVariable Long postId,
            @RequestBody @Valid CommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.createComment(postId, principal.getId(), request));
    }

    /* 댓글 수정 */
    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                commentService.updateComment(commentId, principal.getId(), request)
        );
    }

    /* 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        commentService.deleteComment(commentId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
