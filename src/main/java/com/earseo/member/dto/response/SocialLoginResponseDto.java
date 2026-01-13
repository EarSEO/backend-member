package com.earseo.member.dto.response;

import com.earseo.member.entity.Role;
import lombok.Builder;

@Builder
public record SocialLoginResponseDto(
        Boolean isNewMember,
        String email,
        String provider,
        String nickname,
        String tempToken,

        // 기존 회원인 경우에만 값이 있음
        String accessToken,
        String refreshToken,
        Long memberId,
        Role role
) {
    // 기존 회원용
    public static SocialLoginResponseDto existing(LoginResponseDto loginResponseDto) {
        return SocialLoginResponseDto.builder()
                .isNewMember(false)
                .email(loginResponseDto.email())
                .nickname(loginResponseDto.nickname()) // 기존 닉네임
                .accessToken(loginResponseDto.accessToken())
                .refreshToken(loginResponseDto.refreshToken())
                .memberId(loginResponseDto.memberId())
                .role(loginResponseDto.role())
                .build();
    }

    // 신규 회원용 응답 생성
    public static SocialLoginResponseDto newMember(String email, String provider, String tempToken , String nickname) {
        return SocialLoginResponseDto.builder()
                .isNewMember(true)
                .email(email)
                .provider(provider)
                .tempToken(tempToken)
                .nickname(nickname)
                .build();
    }
}