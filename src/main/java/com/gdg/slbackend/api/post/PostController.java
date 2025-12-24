package com.gdg.slbackend.api.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.post.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities/{communityId}/posts")
@Tag(name = "Post", description = "Community posts")
@SecurityRequirement(name = "bearerAuth")
public class PostController {
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long communityId,
            @ModelAttribute @Valid PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PostResponse response =
                postService.createPost(postRequest, communityId, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/pinned")
    @Operation(summary = "Get pinned post")
    public ResponseEntity<PostResponse> getPinnedPost(
            @PathVariable Long communityId
    ) {
        return postService.getPinnedPost(communityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/{postId}")
    @Operation(summary = "Get post")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long communityId,
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                postService.getPost(communityId, postId)
        );
    }

    @GetMapping
    @Operation(summary = "Get posts with infinite scroll")
    public ResponseEntity<List<PostResponse>> getAllPosts(
            @PathVariable Long communityId,
            @RequestParam(required = false) Long lastId
    ) {
        return ResponseEntity.ok(
                postService.getAllPosts(communityId, lastId)
        );
    }

    @PatchMapping("/{postId}/pin")
    @Operation(summary = "Toggle pinned post")
    public ResponseEntity<PostResponse> updatePinned(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                postService.updatePinned(postId, principal.getId())
        );
    }

    @PatchMapping(
            value = "/{postId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Update post")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestPart("post") @Valid PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                postService.updatePost(postRequest, postId, principal.getId())
        );
    }

    @PatchMapping("/{postId}/likes")
    @Operation(summary = "Update post likes")
    public ResponseEntity<PostResponse> updateLikes(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                postService.updateLikes(postId)
        );
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "Delete post")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        postService.deletePost(postId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}
