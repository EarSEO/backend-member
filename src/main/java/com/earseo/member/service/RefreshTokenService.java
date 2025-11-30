package com.earseo.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
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
        log.info("Refresh Token 저장 완료 - memberId: {}", memberId);
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
        Boolean deleted = stringTemplate.delete(key);
        if (Boolean.TRUE.equals(deleted)) {
            log.info("Refresh Token 삭제 완료 - memberId: {}", memberId);
        } else {
            log.warn("Refresh Token 삭제 실패 또는 존재하지 않음 - memberId: {}", memberId);
        }
    }

    /**
     * Refresh Token 검증 (저장된 값과 일치하는지 확인)
     */
    public boolean validateRefreshToken(Long memberId, String refreshToken) {
        String storedToken = getRefreshToken(memberId);
        return storedToken != null && storedToken.equals(refreshToken);
    }
}