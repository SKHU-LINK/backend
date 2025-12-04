package com.gdg.slbackend.service.post;

import com.gdg.slbackend.api.post.dto.PostRequest;
import com.gdg.slbackend.api.post.dto.PostResponse;
import com.gdg.slbackend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    final private PostCreator postCreator;
    final private S3Uploader s3Uploader;

    public PostResponse createPost(PostRequest postRequest, Long userId) {
        String imageUrl = null;

        if (postRequest.getMultipartFile() != null && !postRequest.getMultipartFile().isEmpty()) {
            imageUrl = s3Uploader.uploadFile(postRequest.getMultipartFile(), "posts");
        }

        return PostResponse.from(
                postCreator.createPost(postRequest, userId, imageUrl)
        );
    }
}
