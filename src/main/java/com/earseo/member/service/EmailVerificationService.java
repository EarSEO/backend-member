package com.earseo.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final RedisTemplate<String, String> stringTemplate;

    private static final String VERIFICATION_PREFIX = "email:verification:";
    private static final String VERIFIED_PREFIX = "email:verified:";
    private static final long EXPIRATION_MINUTES = 5;
    private static final long VERIFIED_EXPIRATION_MINUTES = 10;

    /**
     * 6자리 인증코드 생성
     */
    public String generateCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 인증코드 저장
     */
    public void saveCode(String email, String code) {
        String key = VERIFICATION_PREFIX + email;
        stringTemplate.opsForValue().set(key, code, EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 인증코드 검증
     */
    public boolean verifyCode(String email, String code) {
        String storedCode = stringTemplate.opsForValue().get(VERIFICATION_PREFIX + email);
        return storedCode != null && storedCode.equals(code);
    }

    /**
     * 인증코드 삭제 (인증 성공 후 호출)
     */
    public void deleteCode(String email) {
        String key = VERIFICATION_PREFIX + email;
        stringTemplate.delete(key);
    }

    /**
     * 인증코드 존재 여부 확인
     */
    public boolean hasCode(String email) {
        String key = VERIFICATION_PREFIX + email;
        return Boolean.TRUE.equals(stringTemplate.hasKey(key));
    }

    /**
     * 인증 완료 상태 저장
     */
    public void saveVerified(String email) {
        String key = VERIFIED_PREFIX + email;
        stringTemplate.opsForValue().set(key, "true", VERIFIED_EXPIRATION_MINUTES, TimeUnit.MINUTES);
    }

    /**
     * 인증 완료 여부 확인
     */
    public boolean isVerified(String email) {
        String key = VERIFIED_PREFIX + email;
        return Boolean.TRUE.equals(stringTemplate.hasKey(key));
    }

    /**
     * 인증 완료 상태 삭제 (회원가입 완료 후 호출)
     */
    public void deleteVerified(String email) {
        String key = VERIFIED_PREFIX + email;
        stringTemplate.delete(key);
    }
}