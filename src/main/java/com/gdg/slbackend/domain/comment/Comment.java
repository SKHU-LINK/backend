package com.gdg.slbackend.domain.comment;

import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String content;

    @Column(nullable = false)
    private Long postId;

    @Column(nullable = false)
    private Long authorId;

    private Long likes;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder
    private Comment(String content, Long postId, Long authorId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.content = content;
        this.postId = postId;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void increaseLikes() {
        ++likes;
    }
}
