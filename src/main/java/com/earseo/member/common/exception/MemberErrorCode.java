package com.earseo.member.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements ErrorCodeInterface {

    MEMBER_NOT_FOUND("MBR001", "회원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    DUPLICATE_EMAIL("MBR002", "이미 사용중인 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_PASSWORD("MBR003", "비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    INVALID_CREDENTIALS("MBR004", "이메일 또는 비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    MEMBER_ALREADY_EXISTS("MBR005", "이미 가입된 회원 또는 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),
    UNAUTHORIZED_ACCESS("MBR006", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);

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
