package com.gdg.slbackend.api.auth;

import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.api.auth.dto.RefreshTokenRequest;
import com.gdg.slbackend.api.user.dto.UserResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public ResponseEntity<Void> loginRedirect() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, "/oauth2/authorization/microsoft");
        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
        return ApiResponse.success(authService.toUserResponse(principal));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.success(authService.refreshTokens(request.getRefreshToken()));
    }
}
