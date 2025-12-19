package com.gdg.slbackend.api.community.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommunityUpdateAdminRequest {
    @NotNull
    private Long newAdminUserId;
}
