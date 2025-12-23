package com.gdg.slbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.api.auth.dto.AuthTokenResponse;
import com.gdg.slbackend.global.security.CustomUserDetailsService;
import com.gdg.slbackend.global.security.JwtAuthenticationFilter;
import com.gdg.slbackend.global.security.JwtTokenProvider;
import com.gdg.slbackend.global.security.OAuth2LoginSuccessHandler;
import com.gdg.slbackend.service.auth.AuthService;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;


@Configuration
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Value("${app.frontend.local-callback-url}")
    private String localCallbackUrl;

    @Value("${app.frontend.prod-callback-url}")
    private String prodCallbackUrl;

    public SecurityConfig(
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService,
            AuthService authService,
            ObjectMapper objectMapper
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/auth/login",
                                "/auth/refresh",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/memos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/memos/**").permitAll()
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(ae -> ae.baseUri("/oauth2/authorization"))
                        .successHandler((request, response, authentication) -> {

                            AuthTokenResponse tokenResponse =
                                    authService.handleMicrosoftLogin(
                                            (OAuth2AuthenticationToken) authentication
                                    );

                            String redirectBaseUrl =
                                    resolveFrontendCallbackUrl(request);

                            String redirectUrl = UriComponentsBuilder
                                    .fromUriString(redirectBaseUrl)
                                    .fragment("accessToken=" + tokenResponse.getAccessToken())
                                    .build()
                                    .toUriString();

                            log.info("OAuth2 Login Redirect: {}", redirectUrl);
                            response.sendRedirect(redirectUrl);
                        })
                )

                // 로그아웃
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {

                            String redirectBaseUrl =
                                    resolveFrontendCallbackUrl(request);

                            String redirectUrl = UriComponentsBuilder
                                    .fromUriString(redirectBaseUrl)
                                    .fragment("logout=true")
                                    .build()
                                    .toUriString();

                            log.info("Logout Redirect: {}", redirectUrl);
                            response.sendRedirect(redirectUrl);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        http.addFilterBefore(jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Origin 기준으로 프론트 콜백 URL 결정
     */
    private String resolveFrontendCallbackUrl(HttpServletRequest request) {
        String origin = request.getHeader("Origin");
        log.info("Request Origin: {}", origin);

        if (origin != null && origin.contains("localhost")) {
            return localCallbackUrl;
        }
        return prodCallbackUrl;
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080",
                "https://skhu-link.duckdns.org"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
}
