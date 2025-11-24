package com.earseo.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequestDto(
        @NotBlank(message = "현재 비밀번호는 필수입니다")
        String currentPassword,

        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        String newPassword,

        @NotBlank(message = "비밀번호 확인은 필수입니다")
        String newPasswordConfirm
) {
}