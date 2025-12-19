package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.domain.resource.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResourceCreator {

    private final ResourceRepository resourceRepository;

    @Transactional
    public Resource create(
            Long communityId,
            Long uploaderId,
            String title,
            String fileId
    ) {
        Resource resource = Resource.builder()
                .communityId(communityId)
                .uploaderId(uploaderId)
                .title(title)
                .fileId(fileId)
                .build();

        return resourceRepository.save(resource);
    }
}
