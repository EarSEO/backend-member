package com.earseo.member.dto.response;

import com.earseo.member.entity.Role;

public record SocialLoginResponseDto(
        Boolean isNewMember,
        String email,

        // 기존 회원인 경우에만 값이 있음
        String accessToken,
        String refreshToken,
        Long memberId,
        String nickname,
        Role role
) {
    // 기존 회원용
    public static SocialLoginResponseDto existing(
            LoginResponseDto loginResponseDto
    ) {
        return new SocialLoginResponseDto(
                false,
                loginResponseDto.email(),
                loginResponseDto.accessToken(),
                loginResponseDto.refreshToken(),
                loginResponseDto.memberId(),
                loginResponseDto.nickname(),
                loginResponseDto.role()
        );
    }

    // 신규 회원용 응답 생성
    public static SocialLoginResponseDto newMember(
            String email
    ) {
        return new SocialLoginResponseDto(
                true,
                email,
                null,
                null,
                null,
                null,
                null
        );
    }
}