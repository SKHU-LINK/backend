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

@Component
@RequiredArgsConstructor
public class CommunityMembershipFinder {
    private final CommunityMembershipRepository communityMembershipRepository;

    @Transactional
    public CommunityMembership findByIdOrThrow(Long communityId, Long userId) {
        return communityMembershipRepository
                .findByUserIdAndCommunityId(communityId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @Transactional(readOnly = true)
    public CommunityMembership findAdminMembershipOrThrow(Long communityId, Long userId) {

        CommunityMembership communityMembership = communityMembershipRepository
                .findByUserIdAndCommunityId(communityId, userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

        if (communityMembership.getRole() != Role.ADMIN) {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }

        return communityMembership;
    }

    @Transactional(readOnly = true)
    public boolean isPinned(Long communityId, Long userId) {
        CommunityMembership membership = communityMembershipRepository
                .findByUserIdAndCommunityId(userId, communityId)
                .orElseThrow(() -> new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR));

        return membership.isPinned();
    }

    @Transactional(readOnly = true)
    public List<CommunityMembership> findAllByUserId(Long userId) {
        List<CommunityMembership> memberships = communityMembershipRepository.findAllByUserId(userId);

        if (memberships.isEmpty()) {
            throw new GlobalException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return memberships;
    }

    @Transactional(readOnly = true)
    public boolean isAdmin(Long communityId, Long userId) {
        return communityMembershipRepository.existsByUserIdAndCommunityIdAndRole(
                communityId, userId, Role.ADMIN
        );
    }

}
