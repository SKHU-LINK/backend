package com.gdg.slbackend.api.community.dto;

import lombok.Getter;

@Getter
public class CommunityMembershipRequest {
    private Long communityId;
    private Long userId;
}
