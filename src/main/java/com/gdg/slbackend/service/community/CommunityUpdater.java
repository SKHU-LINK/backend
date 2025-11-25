package com.gdg.slbackend.service.community;

import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.domain.community.Community;
import com.gdg.slbackend.domain.community.CommunityRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CommunityUpdater {
    private final CommunityRepository communityRepository;

//    public Community updateCommunityAdmin(CommunityRequest communityRequest, )
}
