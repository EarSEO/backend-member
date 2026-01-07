package com.earseo.member.dto.request;

public record AppleLoginRequestDto(
        String identityToken,
        String fullName
) {
}
