package com.gdg.slbackend.service.communityMembership;

import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.community.CommunityMembershipRepository;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommunityMembershipFinder {
    private final CommunityMembershipRepository communityMembershipRepository;

    @Transactional
    public CommunityMembership findByIdOrThrow(Long communityId, Long userId) {
        return communityMembershipRepository
                .findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional(readOnly = true)
    public Optional<CommunityMembership> findById(
            Long communityId,
            Long userId
    ) {
        return communityMembershipRepository.findByCommunityIdAndUserId(communityId, userId);
    }

    @Transactional(readOnly = true)
    public CommunityMembership findAdminMembershipOrThrow(Long communityId, Long userId) {
        CommunityMembership communityMembership = communityMembershipRepository
                .findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (communityMembership.getRole() != Role.ADMIN) {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }

        return communityMembership;
    }

    @Transactional(readOnly = true)
    public boolean isPinned(Long communityId, Long userId) {
        CommunityMembership membership = communityMembershipRepository
                .findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

        return membership.isPinned();
    }

    @Transactional(readOnly = true)
    public boolean isCommunityAdmin(Long communityId, Long userId) {
        return communityMembershipRepository
                .existsByCommunityIdAndUserIdAndRole(
                        communityId,
                        userId,
                        Role.ADMIN
                );
    }


    @Transactional(readOnly = true)
    public List<CommunityMembership> findAllByUserId(Long userId) {
        return communityMembershipRepository.findAllByUserId(userId);
    }
}
