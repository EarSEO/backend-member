package com.earseo.member.service.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialSignUpTempService {

    private final StringRedisTemplate stringRedisTemplate;
    private static final String PREFIX = "social:signup:";
    private static final Duration TTL = Duration.ofMinutes(30);

    public String createTempToken(String providerId) {
        String tempToken = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(PREFIX + tempToken, providerId, TTL);
        return tempToken;
    }

    public String getProviderId(String tempToken) {
        return stringRedisTemplate.opsForValue().get(PREFIX + tempToken);
    }

    public void deleteTempToken(String tempToken) {
        stringRedisTemplate.delete(PREFIX + tempToken);
    }
}
