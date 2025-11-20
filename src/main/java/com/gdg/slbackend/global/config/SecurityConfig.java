package com.gdg.slbackend.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdg.slbackend.global.security.CustomUserDetailsService;
import com.gdg.slbackend.global.security.JwtAuthenticationFilter;
import com.gdg.slbackend.global.security.JwtTokenProvider;
import com.gdg.slbackend.global.security.OAuth2LoginSuccessHandler;
import com.gdg.slbackend.service.auth.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
                .csrf(csrf -> csrf.disable())
                // OAuth2 로그인 과정에서는 세션이 필요하므로 IF_REQUIRED로 설정
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/auth/refresh", "/oauth2/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .authorizationEndpoint(ae -> ae.baseUri("/oauth2/authorization"))
                        .redirectionEndpoint(re -> re.baseUri("/auth/microsoft/callback"))
                        .successHandler(new OAuth2LoginSuccessHandler(authService, objectMapper))
                );

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
