package com.gdg.slbackend.service.post;

import com.gdg.slbackend.domain.post.Post;
import com.gdg.slbackend.domain.post.PostRepository;
import com.gdg.slbackend.global.util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PostDeleter {

    private final PostRepository postRepository;
    private final S3Uploader s3Uploader;

    @Transactional
    public void delete(Post post) {
        // S3 파일 삭제
        s3Uploader.deleteFile(post.getImageUrl());

        // DB 삭제
        postRepository.delete(post);
    }
}

