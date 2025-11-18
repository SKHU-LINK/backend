package com.gdg.slbackend.global.exception;

import com.gdg.slbackend.global.response.ApiResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 애플리케이션 전체의 예외를 처리하는 전역 예외 핸들러임.
 * GlobalException, ValidationException, 일반 Exception을 구분하여 처리함.
 * ApiResponse 형태로 오류 응답을 반환하여 전체 API의 에러 응답 형식을 통일함.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ApiResponse<Void> handleCustomException(GlobalException e) {
        return ApiResponse.error(e.getErrorCode().getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ApiResponse.error(message);
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleGeneralException(Exception e) {
        return ApiResponse.error(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }
}
