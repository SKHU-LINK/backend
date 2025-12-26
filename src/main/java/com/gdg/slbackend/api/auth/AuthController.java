package com.gdg.slbackend.api.auth;

import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.api.auth.dto.RefreshTokenRequest;
import com.gdg.slbackend.api.user.dto.UserResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "인증/인가 관련 API")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    @Operation(summary = "마이크로소프트 로그인 리다이렉트")
    public ResponseEntity<Void> loginRedirect(
            @RequestParam(value = "redirect", required = false) String redirect
    ) {
        String location = UriComponentsBuilder
                .fromPath("/oauth2/authorization/microsoft")
                .queryParam("redirect", redirect) // null이면 자동으로 안 붙음
                .build()
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, location);

        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/me")
    @Operation(summary = "내 프로필 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(authService.toUserResponse(principal));
    }

    @PostMapping("/refresh")
    @Operation(summary = "토큰 재발급")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshTokens(request.getRefreshToken()));
    }
}
