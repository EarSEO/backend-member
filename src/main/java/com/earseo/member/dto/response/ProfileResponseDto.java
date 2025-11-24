package com.earseo.member.dto.response;

import com.earseo.member.entity.Gender;

import java.time.LocalDate;

public record ProfileResponseDto(
        Long memberId,
        String email,
        String nickname,
        String profileImage,
        Gender gender,
        LocalDate birthdate,
        String nationality
) {
}
