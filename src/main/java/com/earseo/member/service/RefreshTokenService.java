package com.earseo.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> stringTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";

    /**
     * Refresh Token 저장
     */
    public void saveRefreshToken(Long memberId, String refreshToken, long expirationMs) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        stringTemplate.opsForValue().set(key, refreshToken, expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Refresh Token 조회
     */
    public String getRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        return stringTemplate.opsForValue().get(key);
    }

    /**
     * Refresh Token 존재 여부 확인
     */
    public boolean hasRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        return Boolean.TRUE.equals(stringTemplate.hasKey(key));
    }

    /**
     * Refresh Token 삭제 (로그아웃 시 호출)
     */
    public void deleteRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        stringTemplate.delete(key);
    }

    /**
     * Refresh Token 검증 (저장된 값과 일치하는지 확인)
     */
    public boolean validateRefreshToken(Long memberId, String refreshToken) {
        String storedToken = getRefreshToken(memberId);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}