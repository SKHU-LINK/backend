package com.gdg.slbackend.api.user.dto;

import com.gdg.slbackend.domain.user.User;

public class UserResponse {

    private Long id;
    private String email;
    private String displayName;
    private String nickname;
    private String role;

    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.nickname = user.getNickname();
        this.role = user.getRole().name();
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
}
