package com.gdg.slbackend.service.auth;

import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.api.user.dto.UserResponse;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.domain.user.UserRepository;
import com.gdg.slbackend.domain.user.UserRole;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import com.gdg.slbackend.global.security.JwtTokenProvider;
import com.gdg.slbackend.global.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class AuthService {

    private static final String MICROSOFT_PROVIDER = "MICROSOFT";

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public AuthTokenResponse handleMicrosoftLogin(OAuth2AuthenticationToken authentication) {
        OAuth2User oAuth2User = authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String subject = getAttribute(attributes, "oid", "sub");
        String email = getAttribute(attributes, "email", "preferred_username");
        String displayName = getAttribute(attributes, "name", "displayName");
        String nickname = displayName != null ? displayName : email;

        if (subject == null || email == null) {
            throw new GlobalException(ErrorCode.INVALID_REQUEST);
        }

        User user = userRepository.findByOauthProviderAndOauthSubject(MICROSOFT_PROVIDER, subject)
                .orElseGet(() -> new User(
                        MICROSOFT_PROVIDER,
                        subject,
                        email,
                        displayName,
                        nickname,
                        UserRole.USER
                ));

        if (user.isBanned()) {
            throw new GlobalException(ErrorCode.USER_BANNED);
        }

        user.updateLastLoginAt(LocalDateTime.now());
        if (user.getId() == null) {
            userRepository.save(user);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user);
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        return new AuthTokenResponse(accessToken, refreshToken);
    }

    public AuthTokenResponse refreshTokens(String refreshToken) {
        try {
            Claims claims = jwtTokenProvider.parseClaims(refreshToken);

            if (!jwtTokenProvider.isRefreshToken(claims)) {
                throw new GlobalException(ErrorCode.INVALID_REQUEST);
            }

            User user = userRepository.findById(Long.parseLong(claims.getSubject()))
                    .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

            if (user.isBanned()) {
                throw new GlobalException(ErrorCode.USER_BANNED);
            }

            String newAccessToken = jwtTokenProvider.createAccessToken(user);
            String newRefreshToken = jwtTokenProvider.createRefreshToken(user);

            return new AuthTokenResponse(newAccessToken, newRefreshToken);
        } catch (JwtException | IllegalArgumentException e) {
            throw new GlobalException(ErrorCode.UNAUTHORIZED);
        }
    }

    public UserResponse toUserResponse(UserPrincipal principal) {
        User user = userRepository.findById(principal.getId())
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
        return new UserResponse(user);
    }

    private String getAttribute(Map<String, Object> attributes, String primaryKey, String fallbackKey) {
        Object value = attributes.get(primaryKey);
        if (value == null && fallbackKey != null) {
            value = attributes.get(fallbackKey);
        }
        return value == null ? null : value.toString();
    }
}
