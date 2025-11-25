package com.gdg.slbackend.api.auth.dto;

/**
 * OAuth 로그인 성공 후 발급되는 JWT 토큰 응답.
 */
public class AuthTokenResponse {

    private final String accessToken;
    private final String refreshToken;

    public AuthTokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}