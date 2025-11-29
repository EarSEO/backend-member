package com.earseo.member.dto.response;

public record NicknameCheckResponseDto(
        boolean available,
        String message
) {
}