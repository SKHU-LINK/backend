package com.gdg.slbackend.api.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 리프레시 토큰을 이용해 액세스 토큰을 재발급하기 위한 요청 DTO.
 */
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
}
