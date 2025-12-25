package com.gdg.slbackend.global.security;

import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * MS OAuth 로그인 성공 시 프론트 콜백으로 리다이렉트하며,
 * URL fragment(#)에 accessToken/refreshToken을 담는다.
 */
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;
    private final String localCallbackUrl;
    private final String prodCallbackUrl;

    public OAuth2LoginSuccessHandler(AuthService authService, String localCallbackUrl, String prodCallbackUrl) {
        this.authService = authService;
        this.localCallbackUrl = localCallbackUrl;
        this.prodCallbackUrl = prodCallbackUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("OAuth2LoginSuccessHandler called");

        AuthTokenResponse tokenResponse =
                authService.handleMicrosoftLogin((OAuth2AuthenticationToken) authentication);

        String callbackUrl = resolveCallbackUrl(request);

        String redirectUrl = UriComponentsBuilder
                .fromUriString(callbackUrl)
                .fragment("accessToken=" + tokenResponse.getAccessToken()
                        + "&refreshToken=" + tokenResponse.getRefreshToken())
                .build()
                .toUriString();

        log.info("Redirect to: {}", redirectUrl);

        response.sendRedirect(redirectUrl);
    }

    private String resolveCallbackUrl(HttpServletRequest request) {
        if (request.getSession(false) == null) {
            return prodCallbackUrl;
        }

        Object target = request.getSession(false)
                .getAttribute(OAuth2RedirectTargetFilter.SESSION_ATTR_REDIRECT_TARGET);

        // 한 번 쓰고 제거 (다음 로그인에 영향 주지 않게)
        request.getSession(false)
                .removeAttribute(OAuth2RedirectTargetFilter.SESSION_ATTR_REDIRECT_TARGET);

        if ("local".equals(target)) {
            return localCallbackUrl;
        }

        return prodCallbackUrl;
    }
}
