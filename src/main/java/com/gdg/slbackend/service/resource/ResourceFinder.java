package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.domain.resource.Resource;
import com.gdg.slbackend.domain.resource.ResourceRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResourceFinder {

    private static final int PAGE_SIZE = 10;

    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
    public Resource findByIdOrThrow(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new GlobalException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Resource> findAll(Long communityId, Long lastId) {
        Pageable pageable = PageRequest.of(0, PAGE_SIZE);

        if (lastId == null) {
            return resourceRepository
                    .findByCommunityIdOrderByIdDesc(communityId, pageable);
        }

        return resourceRepository
                .findByCommunityIdAndIdLessThanOrderByIdDesc(
                        communityId, lastId, pageable
                );
    }
}

