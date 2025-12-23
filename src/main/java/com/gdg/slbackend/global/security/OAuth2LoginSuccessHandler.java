package com.gdg.slbackend.global.security;

import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * MS OAuth 로그인 성공 시 JWT를 바로 응답 본문으로 내려주는 핸들러.
 */
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${app.frontend.local-callback-url}")
    private String localCallbackUrl;

    @Value("${app.frontend.prod-callback-url}")
    private String prodCallbackUrl;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("OAuth2LoginSuccessHandler called");

        AuthTokenResponse tokenResponse =
                authService.handleMicrosoftLogin((OAuth2AuthenticationToken) authentication);

        String redirectBaseUrl = resolveRedirectUrl(request);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(redirectBaseUrl)
                .fragment("accessToken=" + tokenResponse.getAccessToken()
                        + "&refreshToken=" + tokenResponse.getRefreshToken())
                .build()
                .toUriString();

        log.info("Redirect to: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String resolveRedirectUrl(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        log.info("Request Origin: {}", origin);

        if (origin != null && origin.contains("localhost")) {
            return localCallbackUrl;
        }
        return prodCallbackUrl;
    }
}
