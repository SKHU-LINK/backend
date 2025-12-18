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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "resources")
public class Resource extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long communityId;

    private Long uploaderId;

    private String title;

    /**
     * S3 object key (ex: resources/uuid_filename.pdf)
     */
    private String fileId;

    @Builder
    public Resource(Long communityId, Long uploaderId, String title, String fileId) {
        this.communityId = communityId;
        this.uploaderId = uploaderId;
        this.title = title;
        this.fileId = fileId;
    }

    public void updateTitle(String title) {
        this.title = title;
    }
}
