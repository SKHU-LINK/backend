package com.gdg.slbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.global.security.CustomUserDetailsService;
import com.gdg.slbackend.global.security.JwtAuthenticationFilter;
import com.gdg.slbackend.global.security.JwtTokenProvider;
import com.gdg.slbackend.global.security.OAuth2LoginSuccessHandler;
import com.gdg.slbackend.service.auth.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

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
                // ✅ CORS 활성화 (중요)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // ===== Swagger / OpenAPI =====
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // ===== Auth =====
                        .requestMatchers(
                                "/auth/login",
                                "/auth/refresh",
                                "/auth/microsoft/callback",
                                "/oauth2/**",
                                "/error"
                        ).permitAll()

                        // ===== Memo =====
                        .requestMatchers(HttpMethod.GET, "/memos/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/memos/**").permitAll()

                        // ===== 읽기 전용 공개 =====
                        .requestMatchers(HttpMethod.GET,
                                "/communities/**",
                                "/posts/**",
                                "/resources/**"
                        ).permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(ae -> ae.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(re -> re.baseUri("/auth/microsoft/callback"))
                        .successHandler(oAuth2LoginSuccessHandler())
                );

        http.addFilterBefore(
                jwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    /**
     * ✅ CORS 설정 (운영 기준)
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000"
                // 👉 여기에 운영 프론트 주소 추가
                // "https://프론트도메인.com"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler() {
        return new OAuth2LoginSuccessHandler(authService, objectMapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
}
