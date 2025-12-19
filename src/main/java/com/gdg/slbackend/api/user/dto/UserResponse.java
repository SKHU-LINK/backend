package com.gdg.slbackend.api.user.dto;

import com.gdg.slbackend.domain.user.User;
import lombok.Getter;

@Getter
public class UserResponse {

    private final Long id;
    private final String email;
    private final String displayName;
    private final String nickname;
    private final String role;
    private final int mileage;
    private final boolean banned;
    private final String oauthProvider;
    private final String lastLoginAt;
    private final String createdAt;
    private final String updatedAt;

    private UserResponse(
            Long id,
            String email,
            String displayName,
            String nickname,
            String role,
            int mileage,
            boolean banned,
            String oauthProvider,
            String lastLoginAt,
            String createdAt,
            String updatedAt
    ) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.nickname = nickname;
        this.role = role;
        this.mileage = mileage;
        this.banned = banned;
        this.oauthProvider = oauthProvider;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getNickname(),
                user.getRole().name(),
                user.getMileage(),
                user.isBanned(),
                user.getOauthProvider(),
                user.getLastLoginAt() == null ? null : user.getLastLoginAt().toString(),
                user.getCreatedAt() == null ? null : user.getCreatedAt().toString(),
                user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString()
        );
    }
}
