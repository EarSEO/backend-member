package com.earseo.member.util;

import com.earseo.member.entity.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.accessTokenExpiration}") long accessTokenExpiration,
            @Value("${jwt.refreshTokenExpiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Access Token 생성
     */
    public String generateAccessToken(Long memberId, String email, Role role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .subject(memberId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token 생성
     */
    public String generateRefreshToken(Long memberId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
                .subject(memberId.toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }
    /**
     * 토큰에서 Claims 추출
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 토큰에서 memberId 추출
     */
    public Long getMemberIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 토큰 만료시간 추출 (Redis TTL 설정용)
     */
    public long getExpiration(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration().getTime();
    }

    /**
     * 토큰 남은 유효시간 계산 (밀리초)
     */
    public long getRemainingTime(String token) {
        long expiration = getExpiration(token);
        return expiration - System.currentTimeMillis();
    }

    /**
     * Refresh Token 만료시간 반환 (밀리초)
     */
    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }
}
