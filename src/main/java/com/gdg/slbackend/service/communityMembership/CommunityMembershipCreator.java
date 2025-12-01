package com.gdg.slbackend.service.communityMembership;

import com.gdg.slbackend.api.community.dto.CommunityMembershipRequest;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.community.CommunityMembershipRepository;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.service.community.CommunityFinder;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CommunityMembershipCreator {
    private final CommunityMembershipRepository communityMembershipRepository;
    private final UserFinder userFinder;
    private final CommunityFinder communityFinder;

    public CommunityMembership createCommunityMembership(CommunityMembershipRequest communityMembershipRequest, Long userId, Role role, boolean pin) {
        CommunityMembership communityMembership = CommunityMembership.builder()
                .role(role)
                .isPinned(pin)
                .isBanned(false)
                .joinedAt(LocalDate.now())
                .user(userFinder.findByIdOrThrow(userId))
                .community(communityFinder.findByIdOrThrow(communityMembershipRequest.getCommunityId()))
                .build();

        return communityMembershipRepository.save(communityMembership);
    }
}
