package com.gdg.slbackend.service.communityMembership;

import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.community.CommunityMembershipRepository;
import com.gdg.slbackend.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityMembershipUpdater {
    private final CommunityMembershipRepository communityMembershipRepository;
    private final CommunityMembershipFinder communityMembershipFinder;

    public void updateRole(Long communityId, Long userId, Role role) {
        CommunityMembership communityMembership = communityMembershipFinder.findByIdOrThrow(communityId, userId);

        communityMembership.updateRole(role);
    }

    public void updateRole(CommunityMembership communityMembership, Role role) {
        communityMembership.updateRole(role);
    }
}
