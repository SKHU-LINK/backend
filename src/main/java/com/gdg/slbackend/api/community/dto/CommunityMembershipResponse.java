package com.gdg.slbackend.api.community.dto;

import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityMembership;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class CommunityMembershipResponse {
    private Long id;
    private String role;
    private boolean isPinned;
    private boolean isBanned;
    private LocalDate joinAt;

    private Long userId;
    private String userNickname;

    private Long communityId;

    public static CommunityMembershipResponse from(CommunityMembership communityMembership, User user, Community community) {
        return CommunityMembershipResponse.builder()
                .id(communityMembership.getId())
                .role(communityMembership.getRole().toString())
                .isPinned(communityMembership.isPinned())
                .isBanned(communityMembership.isBanned())
                .userId(user.getId())
                .userNickname(user.getNickname())
                .communityId(community.getId())
                .build();
    }
}
