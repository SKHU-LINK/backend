package com.gdg.slbackend.domain.resource;

import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long communityId;

    private Long uploaderId;

    private String title;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * S3 object key (ex: resources/uuid_filename.pdf)
     */
    private String imageUrl;

    @Builder
    public Resource(Long communityId, Long uploaderId, String title, String imageUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.communityId = communityId;
        this.uploaderId = uploaderId;
        this.title = title;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
