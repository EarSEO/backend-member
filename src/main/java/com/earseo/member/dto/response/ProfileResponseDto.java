package com.earseo.member.dto.response;

import com.earseo.member.entity.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ProfileResponseDto(
        Long memberId,
        String email,
        String nickname,
        String profileImage,
        Gender gender,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate birthdate,
        String nationality
) {
}
