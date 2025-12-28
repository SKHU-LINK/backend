package com.gdg.slbackend.api.auth.dto;

import lombok.Getter;

/**
 * OAuth 로그인 성공 후 발급되는 JWT 토큰 응답. cicd테스트
 */
@Getter
public class AuthTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
