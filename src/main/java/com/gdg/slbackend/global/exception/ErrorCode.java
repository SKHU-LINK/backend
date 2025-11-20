package com.gdg.slbackend.global.exception;

/**
 * 서비스 전역에서 사용되는 에러 코드를 정의함.
 * 각 에러 코드는 사용자에게 보여줄 메시지를 포함함.
 * GlobalException 및 예외 처리 로직에서 참조되어 일관된 예외 처리를 가능하게 함.
 */

public enum ErrorCode {

    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_SERVER_ERROR("서버 내부 오류가 발생했습니다."),

    UNAUTHORIZED("인증이 필요합니다."),
    FORBIDDEN("권한이 없습니다."),

    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_BANNED("차단된 사용자입니다."),
    COMMUNITY_NOT_FOUND("커뮤니티를 찾을 수 없습니다."),
    ALREADY_JOINED("이미 가입된 상태입니다."),
    NOT_COMMUNITY_MEMBER("커뮤니티 멤버가 아닙니다."),
    INVALID_REPORT_TARGET("게시글 또는 댓글 중 하나만 신고해야 합니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}