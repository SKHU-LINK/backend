package com.gdg.slbackend.service.community;

import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityFinder {
    private final CommunityRepository communityRepository;

    @Transactional(readOnly = true)
    public Community findByIdOrThrow(Long communityId) {
        return communityRepository.findById(communityId)
                .orElseThrow(() -> new GlobalException(ErrorCode.COMMUNITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<Community> findAll() {
        return communityRepository.findAll();
    }
}
