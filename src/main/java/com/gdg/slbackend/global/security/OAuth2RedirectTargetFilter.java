package com.gdg.slbackend.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * OAuth2 로그인 시작 요청에서 redirect=local|prod 값을 세션에 저장한다.
 *
 * 흐름:
 * 프론트 -> GET /auth/login?redirect=prod
 * 백엔드 -> 302 /oauth2/authorization/microsoft?redirect=prod
 * 이 필터가 /oauth2/authorization/** 요청에서 redirect 값을 읽어 세션에 저장
 * 이후 OAuth 성공 시 SuccessHandler가 세션 값을 보고 local/prod 콜백 선택
 */
@Component
public class OAuth2RedirectTargetFilter extends OncePerRequestFilter {

    public static final String SESSION_ATTR_REDIRECT_TARGET = "OAUTH2_REDIRECT_TARGET";

    private static final String OAUTH2_AUTHORIZATION_PREFIX = "/oauth2/authorization/";
    private static final String PARAM_REDIRECT = "redirect";
    private static final String TARGET_LOCAL = "local";
    private static final String TARGET_PROD = "prod";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isOAuth2AuthorizationRequest(request)) {
            String redirect = request.getParameter(PARAM_REDIRECT);
            String target = normalizeTarget(redirect);

            if (target != null) {
                request.getSession(true).setAttribute(SESSION_ATTR_REDIRECT_TARGET, target);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOAuth2AuthorizationRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith(OAUTH2_AUTHORIZATION_PREFIX);
    }

    private String normalizeTarget(String redirect) {
        if (redirect == null) {
            return null;
        }

        String value = redirect.trim().toLowerCase(Locale.ROOT);

        if (TARGET_LOCAL.equals(value)) {
            return TARGET_LOCAL;
        }

        if (TARGET_PROD.equals(value)) {
            return TARGET_PROD;
        }

        return null;
    }
}
