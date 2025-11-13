package com.earseo.member.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCodeInterface {

    INVALID_INPUT_VALUE("CMN001", "입력값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("CMN002", "인증이 필요합니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("CMN003", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("CMN004", "요청한 리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    METHOD_NOT_ALLOWED("CMN005", "허용되지 않은 메소드입니다.", HttpStatus.METHOD_NOT_ALLOWED),
    INTERNAL_SERVER_ERROR("CMN006", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_TOKEN("CMN007", "유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("CMN008", "만료된 토큰입니다.", HttpStatus.UNAUTHORIZED);

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