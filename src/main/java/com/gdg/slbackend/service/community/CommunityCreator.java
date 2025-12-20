package com.gdg.slbackend.service.community;

import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityRepository;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.domain.user.UserRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommunityCreator {
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    protected Community create(CommunityRequest communityRequest, User admin) {
        if (communityRequest.getYear() < 1900 || communityRequest.getYear() > 3000){
            throw new GlobalException(ErrorCode.COMMUNITY_INVALID_YEAR);
        }

        /*if (communityRequest.getSemester() != 1 || communityRequest.getSemester() != 2){
            throw new GlobalException(ErrorCode.COMMUNITY_INVALID_SEMESTER);
        }*/

        if (!userRepository.existsById(admin.getId())) {
            throw new GlobalException(ErrorCode.USER_NOT_FOUND);
        }

        Community community = Community.builder()
                .name(communityRequest.getName())
                .year(communityRequest.getYear())
                .semester(communityRequest.getSemester())
                .admin(admin)
                .build();

        return communityRepository.save(community);
    }
}
