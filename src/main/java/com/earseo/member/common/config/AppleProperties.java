package com.earseo.member.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.apple")
public record AppleProperties(
        String teamId,
        String clientId,
        String keyId,
        String redirectUri,
        String privateKey,
        String publicKeyUrl
) {
}