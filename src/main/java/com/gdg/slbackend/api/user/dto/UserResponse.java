package com.gdg.slbackend.api.user.dto;

import com.gdg.slbackend.domain.user.User;

public class UserResponse {

    private Long id;
    private String email;
    private String displayName;
    private String nickname;
    private String role;
    private int mileage;
    private boolean isBanned;
    private String oauthProvider;
    private String lastLoginAt;
    private String createdAt;
    private String updatedAt;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.nickname = user.getNickname();
        this.role = user.getRole().name();
        //this.mileage = user.getMileage();
        this.isBanned = user.isBanned();
        this.oauthProvider = user.getOauthProvider();
        this.lastLoginAt = user.getLastLoginAt() == null ? null : user.getLastLoginAt().toString();
        this.createdAt = user.getCreatedAt() == null ? null : user.getCreatedAt().toString();
        this.updatedAt = user.getUpdatedAt() == null ? null : user.getUpdatedAt().toString();
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public String getRole() {
        return role;
    }

    public int getMileage() {
        return mileage;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }
}