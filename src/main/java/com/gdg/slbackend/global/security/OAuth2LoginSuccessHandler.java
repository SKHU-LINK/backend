package com.gdg.slbackend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * MS OAuth 로그인 성공 시 JWT를 바로 응답 본문으로 내려주는 핸들러.
 */
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend.prod-callback-url}")
    private String frontDomain;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("OAuth2LoginSuccessHandler called");

        AuthTokenResponse tokenResponse =
                authService.handleMicrosoftLogin((OAuth2AuthenticationToken) authentication);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(frontDomain)
                .fragment("accessToken=" + tokenResponse.getAccessToken()
                        + "&refreshToken=" + tokenResponse.getRefreshToken())
                .build()
                .toUriString();

        log.info("Redirect to: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }
}
