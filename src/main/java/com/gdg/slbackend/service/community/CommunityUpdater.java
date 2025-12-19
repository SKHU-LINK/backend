package com.gdg.slbackend.service.community;

import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.community.CommunityRepository;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipUpdater;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityUpdater {
    private final CommunityRepository communityRepository;
    private final CommunityFinder communityFinder;
    private final CommunityMembershipFinder communityMembershipFinder;
    private final CommunityMembershipUpdater communityMembershipUpdater;
    private final UserFinder userFinder;

    public Community updateCommunityAdmin(Long communityId, Long userId) {
        Community community = communityFinder.findByIdOrThrow(communityId);
        User user = userFinder.findByIdOrThrow(userId);
        CommunityMembership communityMembership = communityMembershipFinder.findByIdOrThrow(communityId, userId);

        communityMembershipUpdater.updateRole(communityId, community.getAdmin().getId(), Role.MEMBER);
        communityMembershipUpdater.updateRole(communityMembership, Role.ADMIN);

        community.updateAdmin(user);

        return community;
    }
}
