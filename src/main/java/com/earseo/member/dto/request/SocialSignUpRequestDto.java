package com.earseo.member.dto.request;

import com.earseo.member.entity.Gender;
import com.earseo.member.entity.Provider;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SocialSignUpRequestDto(
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        String email,

        @NotNull(message = "Provider는 필수입니다.")
        Provider provider,

        @NotBlank(message = "providerId는 필수입니다.")
        String providerId,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
        String nickname,

        Gender gender,
        LocalDate birthdate,
        String nationality
) {
}