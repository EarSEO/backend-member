package com.earseo.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TokenRefreshRequestDto(
        @NotBlank(message = "Refresh Token은 필수입니다.")
        String refreshToken
) {
}