package com.earseo.member.dto.response;

public record TokenRefreshResponseDto(
        String accessToken,
        String refreshToken
) {
}