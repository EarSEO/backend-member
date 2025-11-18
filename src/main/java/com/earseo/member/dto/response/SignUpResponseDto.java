package com.earseo.member.dto.response;

import com.earseo.member.entity.Role;

public record SignUpResponseDto(
        Long memberId,
        String email,
        String nickname,
        Role role
) {
}
