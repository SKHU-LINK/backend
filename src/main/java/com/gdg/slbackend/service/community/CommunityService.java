package com.gdg.slbackend.service.community;

import com.gdg.slbackend.api.community.dto.CommunityMembershipRequest;
import com.gdg.slbackend.api.community.dto.CommunityMembershipResponse;
import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.api.community.dto.CommunityResponse;
import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.enums.Role;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipCreator;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipFinder;
import com.gdg.slbackend.service.communityMembership.CommunityMembershipUpdater;
import com.gdg.slbackend.service.user.UserFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityCreator communityCreator;
    private final CommunityFinder communityFinder;
    private final CommunityUpdater communityUpdater;
    private final CommunityDeleter communityDeleter;
    private final UserFinder userFinder;
    private final CommunityMembershipCreator communityMembershipCreator;
    private final CommunityMembershipFinder communityMembershipFinder;
    private final CommunityMembershipUpdater communityMembershipUpdater;

    /* 커뮤니티 생성 */
    @Transactional
    public CommunityResponse createCommunity(
            CommunityRequest communityRequest,
            UserPrincipal principal
    ) {
        User user = userFinder.findByIdOrThrow(principal.getId());
        Community community = communityCreator.create(communityRequest, user);

        communityMembershipCreator.createCommunityMembership(
                new CommunityMembershipRequest(community.getId()),
                user.getId(),
                Role.ADMIN,
                true
        );

        return CommunityResponse.from(community);
    }

    /* 커뮤니티 단건 조회 */
    @Transactional(readOnly = true)
    public CommunityResponse getCommunity(Long communityId, UserPrincipal principal) {

        Community community = communityFinder.findByIdOrThrow(communityId);

        Optional<CommunityMembership> membershipOpt =
                communityMembershipFinder.findById(
                        communityId,
                        principal.getId()
                );

        CommunityMembershipResponse membershipResponse =
                membershipOpt
                        .map(m -> CommunityMembershipResponse.from(
                                m,
                                m.getUser(), // readOnly 트랜잭션 → 안전
                                community
                        ))
                        .orElse(null);

        return CommunityResponse.from(community, membershipResponse);
    }

    /* 커뮤니티 전체 조회 */
    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunityAll(UserPrincipal principal) {

        List<Community> communities = communityFinder.findAll();

        List<CommunityMembership> memberships =
                communityMembershipFinder.findAllByUserId(principal.getId());

        Map<Long, CommunityMembership> membershipMap =
                memberships.stream()
                        .collect(Collectors.toMap(
                                m -> m.getCommunity().getId(),
                                m -> m
                        ));

        return communities.stream()
                .sorted((c1, c2) -> {
                    boolean p1 = membershipMap.get(c1.getId()) != null
                            && membershipMap.get(c1.getId()).isPinned();
                    boolean p2 = membershipMap.get(c2.getId()) != null
                            && membershipMap.get(c2.getId()).isPinned();
                    return Boolean.compare(p2, p1);
                })
                .map(c -> {
                    CommunityMembership m = membershipMap.get(c.getId());
                    CommunityMembershipResponse mr =
                            m == null ? null
                                    : CommunityMembershipResponse.from(
                                    m,
                                    m.getUser(),
                                    c
                            );
                    return CommunityResponse.from(c, mr);
                })
                .toList();
    }

    /* 커뮤니티 관리자 변경 */
    @Transactional
    public CommunityResponse updateCommunityAdmin(
            Long communityId,
            UserPrincipal principal,
            Long newAdminUserId
    ) {
        if (!communityMembershipFinder.isAdmin(communityId, principal.getId())) {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }

        if (!userFinder.isSystemAdmin(principal.getId())) {
            throw new GlobalException(ErrorCode.USER_NOT_SYSTEM_ADMIN);
        }

        Community updated = communityUpdater.updateCommunityAdmin(
                communityId,
                newAdminUserId
        );

        return CommunityResponse.from(updated);
    }

    /* 커뮤니티 고정(pinned) 토글 */
    @Transactional
    public CommunityResponse updateCommunityPinned(
            Long communityId,
            UserPrincipal principal
    ) {
        Community community = communityFinder.findByIdOrThrow(communityId);

        User user = userFinder.findByIdOrThrow(principal.getId());

        Optional<CommunityMembership> membershipOpt =
                communityMembershipFinder.findById(communityId, user.getId());

        CommunityMembership membership;

        if (membershipOpt.isPresent()) {
            membership = membershipOpt.get();
            communityMembershipUpdater.updatePinned(membership);
        } else {
            membership = communityMembershipCreator
                    .createCommunityMembershipByCommunityId(
                            communityId,
                            user.getId(),
                            Role.MEMBER,
                            true
                    );
        }

        CommunityMembershipResponse membershipResponse =
                CommunityMembershipResponse.from(
                        membership,
                        user,       // ✅ Lazy 문제 제거
                        community
                );

        return CommunityResponse.from(community, membershipResponse);
    }

    /* 커뮤니티 삭제 */
    @Transactional
    public void deleteCommunity(Long communityId, UserPrincipal principal) {

        if (!communityMembershipFinder.isAdmin(communityId, principal.getId())) {
            throw new GlobalException(ErrorCode.COMMUNITY_NOT_ADMIN);
        }

        communityDeleter.deleteById(communityId);
    }
}
