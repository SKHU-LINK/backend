package com.gdg.slbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.global.security.CustomUserDetailsService;
import com.gdg.slbackend.global.security.JwtAuthenticationFilter;
import com.gdg.slbackend.global.security.JwtTokenProvider;
import com.gdg.slbackend.global.security.OAuth2LoginSuccessHandler;
import com.gdg.slbackend.global.security.OAuth2RedirectTargetFilter;
import com.gdg.slbackend.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;
    private final OAuth2RedirectTargetFilter oAuth2RedirectTargetFilter;

    private final String localCallbackUrl;
    private final String prodCallbackUrl;

    public SecurityConfig(
            JwtTokenProvider jwtTokenProvider,
            CustomUserDetailsService userDetailsService,
            AuthService authService,
            ObjectMapper objectMapper,
            OAuth2RedirectTargetFilter oAuth2RedirectTargetFilter,
            @Value("${app.frontend.local-callback-url}") String localCallbackUrl,
            @Value("${app.frontend.prod-callback-url}") String prodCallbackUrl
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.authService = authService;
        this.objectMapper = objectMapper;
        this.oAuth2RedirectTargetFilter = oAuth2RedirectTargetFilter;
        this.localCallbackUrl = localCallbackUrl;
        this.prodCallbackUrl = prodCallbackUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CORS 활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // Swagger / OpenAPI 허용
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 인증 관련 허용
                        .requestMatchers(
                                "/auth/login",
                                "/auth/refresh",
                                "/oauth2/**",
                                "/login/oauth2/**",
                                "/error"
                        ).permitAll()

                        // 공개 API
                        .requestMatchers(HttpMethod.GET, "/memos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/memos/**").permitAll()

                        // 그 외 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(ae -> ae.baseUri("/oauth2/authorization"))
                        .successHandler(oAuth2LoginSuccessHandler())
                )

                // 로그아웃 시 리다이렉트
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {

                            String callbackUrl = resolveCallbackUrl(request);

                            String redirectUrl = UriComponentsBuilder
                                    .fromUriString(callbackUrl)
                                    .fragment("logout=true")
                                    .build()
                                    .toUriString();

                            response.setStatus(HttpServletResponse.SC_FOUND);
                            response.sendRedirect(redirectUrl);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        // 로그인 시작(/auth/login -> /oauth2/authorization/microsoft?redirect=xxx) 때 redirect 값 세션 저장
        http.addFilterBefore(oAuth2RedirectTargetFilter, OAuth2AuthorizationRequestRedirectFilter.class);

        // JWT 필터 추가
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private String resolveCallbackUrl(HttpServletRequest request) {
        // 1) 명시적으로 redirect 파라미터가 있으면 그걸 우선
        String redirect = request.getParameter("redirect");
        if ("local".equalsIgnoreCase(redirect)) {
            return localCallbackUrl;
        }
        if ("prod".equalsIgnoreCase(redirect)) {
            return prodCallbackUrl;
        }

        // 2) Origin으로 추정
        String origin = request.getHeader("Origin");
        if (origin != null && origin.contains("localhost")) {
            return localCallbackUrl;
        }
        if (origin != null && origin.contains("vercel.app")) {
            return prodCallbackUrl;
        }

        // 3) 기본값은 prod
        return prodCallbackUrl;
    }

    /**
     * CORS 설정
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",          // 로컬 프론트
                "http://localhost:8080",          // Swagger UI
                "https://skhu-link.duckdns.org",  // 백엔드 도메인(필요시)
                "https://skhu-link.vercel.app"    // 배포 프론트
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(authService, localCallbackUrl, prodCallbackUrl);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
}
