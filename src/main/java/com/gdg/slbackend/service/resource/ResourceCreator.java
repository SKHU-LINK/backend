package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.domain.resource.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ResourceCreator {

    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource create(
            Long communityId,
            Long uploaderId,
            String uploaderNickname,
            String title,
            String imageUrl
    ) {
        Resource resource = Resource.builder()
                .communityId(communityId)
                .uploaderId(uploaderId)
                .uploaderNickname(uploaderNickname)
                .title(title)
                .imageUrl(imageUrl)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return resourceRepository.save(resource);
    }
}
