package com.gdg.slbackend.global.exception;

/**
 * 서비스 전역에서 발생하는 비즈니스 예외를 표현하는 커스텀 예외 클래스임.
 * ErrorCode를 포함하여 어떤 오류인지 명확히 구분할 수 있게 함.
 * 도메인/서비스 레이어에서 검증 실패 시 이 예외를 던지도록 함.
 */

public class GlobalException extends RuntimeException {

    private final ErrorCode errorCode;

    public GlobalException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
