package com.earseo.backend_member.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 예시용 에러 코드 Enum
 * 실제 서비스에서는 별도 Enum 작성 (ex. StoryError, UserError 등)
 */
@Getter
@AllArgsConstructor
public enum SampleError implements ErrorCodeInterface {

    SAMPLE_NOT_FOUND("SMP001", "해당 데이터를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SAMPLE_FORBIDDEN("SMP002", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    SAMPLE_INTERNAL_ERROR("SMP003", "내부 서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
