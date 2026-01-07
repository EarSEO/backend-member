package com.earseo.member.service.oauth;

import com.earseo.member.common.config.AppleProperties;
import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.response.ApplePublicKeyResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplePublicKeyService {
    private final AppleProperties appleProperties;
    private final RestClient restClient;

    public ApplePublicKeyResponseDto getApplePublicKeys() {
        try {
            return restClient.get()
                    .uri(appleProperties.publicKeyUrl())
                    .retrieve()
                    .body(ApplePublicKeyResponseDto.class);
        } catch (RestClientException e) {
            log.error("Apple 공개키 서버 호출 실패: ", e);
            throw new BaseException(MemberErrorCode.APPLE_SERVER_ERROR);
        }
    }
}