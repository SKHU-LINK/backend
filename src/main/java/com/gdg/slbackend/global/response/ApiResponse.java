package com.gdg.slbackend.global.response;

/**
 * API 응답의 공통 포맷을 정의함.
 * 성공 여부(success), 응답 데이터(data), 메시지(message)를 포함함.
 * 모든 컨트롤러가 이 구조를 사용해 일관된 응답을 반환할 수 있도록 함.
 */

public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
