package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    final private PostCreator postCreator;

    public PostResponse createPost(PostRequest postRequest, Long userId) {
        return PostResponse.from(postCreator.createPost(postRequest, userId));
    }
}
