package com.earseo.member.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCodeInterface {

    MEMBER_NOT_FOUND("MEM001", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("MEM002", "이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("MEM003", "이미 사용중인 닉네임입니다.", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS("MEM004", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    SOCIAL_MEMBER_CANNOT_CHANGE_PASSWORD("MEM005", "소셜 로그인 회원은 비밀번호를 변경할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD("MEM006", "현재 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("MEM007", "새 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String status;
    private final String message;
    private final HttpStatus httpStatus;

    @Override
    public ErrorCode getErrorCode() {
        return ErrorCode.builder()
                .status(status)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
