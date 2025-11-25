package com.gdg.slbackend.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

/**
 * MS OAuth 로그인 성공 시 JWT를 바로 응답 본문으로 내려주는 핸들러.
 */
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final ObjectMapper objectMapper;

    public OAuth2LoginSuccessHandler(AuthService authService, ObjectMapper objectMapper) {
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthTokenResponse tokenResponse = authService.handleMicrosoftLogin((OAuth2AuthenticationToken) authentication);

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.success(tokenResponse)));
    }
}