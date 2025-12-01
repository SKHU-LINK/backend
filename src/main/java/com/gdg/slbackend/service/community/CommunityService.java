package com.gdg.slbackend.service.community;

import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.api.community.dto.CommunityResponse;
import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {
    private final CommunityCreator communityCreator;
    private final CommunityFinder communityFinder;
    private final CommunityUpdater communityUpdater;
    private final CommunityDeleter communityDeleter;
    private final UserFinder userFinder;
    private final CommunityMembershipFinder communityMembershipFinder;

    public CommunityResponse createCommunity(CommunityRequest communityRequest, Long userId) {
        User user = userFinder.findByIdOrThrow(userId);

        return CommunityResponse.from(communityCreator.create(communityRequest, user));
    }

    @Transactional(readOnly = true)
    public CommunityResponse getCommunity(Long communityId) {
        Community community = communityFinder.findByIdOrThrow(communityId);

        return CommunityResponse.from(community);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunityAll(Long userId) {
        List<Community> communities = communityFinder.findAll();

        List<CommunityMembership> memberships = communityMembershipFinder.findAllByUserId(userId);

        Map<Long, Boolean> pinnedMap = memberships.stream()
                .collect(Collectors.toMap(
                        m -> m.getCommunity().getId(),
                        CommunityMembership::isPinned
                ));

        return communities.stream()
                .sorted((c1, c2) -> Boolean.compare(
                        pinnedMap.getOrDefault(c2.getId(), false),
                        pinnedMap.getOrDefault(c1.getId(), false)
                ))
                .map(CommunityResponse::from)
                .toList();
    }

    @Transactional
    public CommunityResponse updateCommunityAdmin(Long communityId, Long newAdminUserId, Long requestUserId) {
        /* 해당 커뮤니티의 관리자가 아닌 경우 */
        if(!communityMembershipFinder.isAdmin(communityId, requestUserId)) {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }
        /* 시스템 관리자가 아닌 경우 */
        if(!userFinder.isSystemAdmin(requestUserId)) {
            throw new GlobalException(ErrorCode.USER_NOT_SYSTEM_ADMIN);
        }

        return CommunityResponse.from(communityUpdater.updateCommunityAdmin(communityId, newAdminUserId));
    }

    public void deleteCommunity(Long communityId, Long userId) {
        if (communityMembershipFinder.isAdmin(userId, communityId)) {
            communityDeleter.deleteById(communityId);
        }
        else {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }
    }
}
