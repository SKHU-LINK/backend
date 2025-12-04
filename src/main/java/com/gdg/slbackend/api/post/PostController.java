package com.gdg.slbackend.api.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @ModelAttribute PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PostResponse response = postService.createPost(postRequest, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
