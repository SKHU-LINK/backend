package com.gdg.slbackend.domain.post;

import com.gdg.slbackend.global.entity.BaseTimeEntity;
import com.gdg.slbackend.global.enums.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "posts")
@NoArgsConstructor
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Category category;
    private String title;
    private String content;
    private Long views;
    private Long likes;
    private boolean pinned;

    private Long communityId;
    private Long authorId;
    private String authorNickname;
    private String imageUrl;


//    필요성에 대한 의논 필요
    private LocalDate deletedAt;

    @Builder
    public Post(Long communityId, Long authorId, String authorNickname, String imageUrl, String title, String content, Category category, boolean pinned, Long views, LocalDate deletedAt) {
        this.communityId = communityId;
        this.authorId = authorId;
        this.authorNickname = authorNickname;
        this.imageUrl = imageUrl;
        this.title = title;
        this.content = content;
        this.category = category;
        this.pinned = pinned;
        this.views = views;
        this.deletedAt = deletedAt;
    }

    public void updateAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public void updateThumbnailFileId(String thumbnailFileId) {
        this.imageUrl = imageUrl;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void togglePinned() {
        this.pinned = !pinned;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }

    public void increaseViews() {
        ++views;
    }

    public void increaseLikes() {
        ++likes;
    }

    public void updateImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
