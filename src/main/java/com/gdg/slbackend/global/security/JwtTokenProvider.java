package com.gdg.slbackend.global.security;

import com.gdg.slbackend.domain.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 생성 및 검증을 담당함.
 */
@Component
public class JwtTokenProvider {

    private static final String CLAIM_TOKEN_TYPE = "type";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    private static final String TOKEN_TYPE_REFRESH = "REFRESH";

    private final SecretKey secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public JwtTokenProvider(
            @Value("${security.jwt.secret:ZmFrZS1zZWNyZXQtZm9yLXRlc3QtMTIzNDU2Nzg5MGFiY2RlZg==}") String secret,
            @Value("${security.jwt.access-expiration-minutes:60}") long accessTokenValidity,
            @Value("${security.jwt.refresh-expiration-days:14}") long refreshTokenValidity
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
    }

    public String createAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(accessTokenValidity, ChronoUnit.MINUTES);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim("role", user.getRole().name())
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(User user) {
        Instant now = Instant.now();
        Instant expiry = now.plus(refreshTokenValidity, ChronoUnit.DAYS);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiry))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isRefreshToken(Claims claims) {
        Object type = claims.get(CLAIM_TOKEN_TYPE);
        return TOKEN_TYPE_REFRESH.equals(type);
    }
}
