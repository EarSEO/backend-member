package com.earseo.member.service.oauth;

import com.earseo.member.common.config.AppleProperties;
import com.earseo.member.common.exception.BaseException;
import com.earseo.member.common.exception.MemberErrorCode;
import com.earseo.member.dto.response.ApplePublicKeyResponseDto.ApplePublicKey;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleTokenVerifier {

    private final ApplePublicKeyService applePublicKeyService;
    private final AppleProperties appleProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Claims verifyAndGetClaims(String identityToken) {
        try {
            Map<String, String> header = parseHeader(identityToken);
            String kid = header.get("kid");
            String alg = header.get("alg");
            
            ApplePublicKey matchedKey = applePublicKeyService.getApplePublicKeys()
                    .keys()
                    .stream()
                    .filter(key -> key.kid().equals(kid) && key.alg().equals(alg))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(MemberErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND));
            
            PublicKey publicKey = generatePublicKey(matchedKey);
            
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .requireIssuer("https://appleid.apple.com")
                    .requireAudience(appleProperties.clientId())
                    .build()
                    .parseSignedClaims(identityToken)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new BaseException(MemberErrorCode.APPLE_TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new BaseException(MemberErrorCode.APPLE_TOKEN_INVALID);
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("Apple 토큰 처리 중 오류: ", e);
            throw new BaseException(MemberErrorCode.APPLE_TOKEN_INVALID);
        }
    }

    private Map<String, String> parseHeader(String token) {
        try {
            String headerPart = token.split("\\.")[0];
            byte[] decodedBytes = Base64.getUrlDecoder().decode(headerPart);
            String headerJson = new String(decodedBytes);
            
            Map<String, String> headerMap = objectMapper.readValue(headerJson, Map.class);

            return Map.of(
                    "kid", headerMap.get("kid"),
                    "alg", headerMap.get("alg")
            );
        } catch (Exception e) {
            throw new BaseException(MemberErrorCode.APPLE_TOKEN_INVALID);
        }
    }

    private PublicKey generatePublicKey(ApplePublicKey applePublicKey) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(applePublicKey.n());
            byte[] eBytes = Base64.getUrlDecoder().decode(applePublicKey.e());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("Apple 공개키 생성 실패: ", e);
            throw new BaseException(MemberErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
        }
    }
}