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
    PASSWORD_MISMATCH("MEM007", "새 비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    ALREADY_REGISTERED_WITH_DIFFERENT_PROVIDER("MEM008", "이미 다른 방식으로 가입된 이메일입니다.", HttpStatus.CONFLICT),
    INVALID_REFRESH_TOKEN("MEM009", "유효하지 않은 Refresh Token입니다.", HttpStatus.UNAUTHORIZED),
    EMAIL_SEND_FAILED("MEM010", "이메일 발송에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_VERIFICATION_CODE("MEM011", "유효하지 않은 인증코드입니다.", HttpStatus.BAD_REQUEST),
    VERIFICATION_CODE_EXPIRED("MEM012", "인증코드가 만료되었습니다.", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED("MEM013", "이메일 인증이 완료되지 않았습니다.", HttpStatus.BAD_REQUEST),
    SAME_AS_CURRENT_PASSWORD( "MEM014", "기존 비밀번호와 동일한 비밀번호는 사용할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Apple 로그인 관련
    APPLE_TOKEN_INVALID("MEM015", "유효하지 않은 Apple 토큰입니다.", HttpStatus.UNAUTHORIZED),
    APPLE_PUBLIC_KEY_NOT_FOUND("MEM016", "Apple 공개키를 찾을 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    APPLE_TOKEN_EXPIRED("MEM017", "만료된 Apple 토큰입니다.", HttpStatus.UNAUTHORIZED),
    APPLE_SERVER_ERROR("MEM018", "Apple 서버 연동 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    INVALID_TEMP_TOKEN("MEM019", "유효하지 않거나 만료된 토큰입니다.", HttpStatus.BAD_REQUEST),

    INVALID_GOOGLE_TOKEN( "MEM020", "유효하지 않은 Google 토큰입니다.",HttpStatus.UNAUTHORIZED);

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
