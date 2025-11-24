package com.earseo.member.dto.request;

import com.earseo.member.entity.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ProfileUpdateRequestDto(
        @NotBlank(message = "닉네임은 필수입니다")
        @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다")
        String nickname,

        @NotNull(message = "성별은 필수입니다")
        Gender gender,

        @NotNull(message = "생년월일은 필수입니다")
        LocalDate birthdate,

        @NotBlank(message = "국적은 필수입니다")
        @Size(max = 100, message = "국적은 100자 이하여야 합니다")
        String nationality
) {
}