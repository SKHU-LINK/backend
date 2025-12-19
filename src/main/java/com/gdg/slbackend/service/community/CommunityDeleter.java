package com.gdg.slbackend.service.community;

import com.gdg.slbackend.domain.community.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityDeleter {
    private final CommunityRepository communityRepository;

    public void deleteById(Long communityId) {
        communityRepository.deleteById(communityId);
    }
}
