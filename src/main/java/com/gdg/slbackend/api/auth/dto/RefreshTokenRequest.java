package com.gdg.slbackend.api.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 리프레시 토큰을 이용해 액세스 토큰을 재발급하기 위한 요청 DTO.
 */
@Getter
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank
    private String refreshToken;
}
