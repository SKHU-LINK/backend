package com.gdg.slbackend.api.community.dto;

import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityMembership;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommunityResponse {
    private Long id;
    private String name;
    private int year;
    private int semester;
    private String adminNickname;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CommunityMembershipResponse communityMembershipResponse;

    public static CommunityResponse from(Community community, CommunityMembershipResponse communityMembershipResponse) {
        return CommunityResponse.builder()
                .id(community.getId())
                .name(community.getName())
                .year(community.getYear())
                .semester(community.getSemester())
                .adminNickname(community.getAdmin().getNickname())
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .communityMembershipResponse(communityMembershipResponse)
                .build();
    }

    public static CommunityResponse from(Community community) {
        return from(community, null);
    }
}
