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
    USER_NOT_SYSTEM_ADMIN("시스템 관리자만 수행할 수 있는 작업입니다."),

    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    USER_BANNED("차단된 사용자입니다."),
    COMMUNITY_NOT_FOUND("커뮤니티를 찾을 수 없습니다."),
    ALREADY_JOINED("이미 가입된 상태입니다."),
    NOT_COMMUNITY_MEMBER("커뮤니티 멤버가 아닙니다."),
    INVALID_REPORT_TARGET("게시글 또는 댓글 중 하나만 신고해야 합니다."),
    INSUFFICIENT_MILEAGE("마일리지가 부족합니다."),

    /**
     * 커뮤니티 관련 에러 코드
     * */
    COMMUNITY_INVALID_YEAR("연도는 1900부터 3000 사이여야 합니다."),
    COMMUNITY_INVALID_SEMESTER("학기는 1 또는 2여야 합니다."),
    COMMUNITY_NOT_ADMIN("커뮤니티 관리자만 수행할 수 있는 작업입니다."),

    /**
     * 게시물 관련 에러 코드
     * */
    POST_NOT_FOUND("게시물이 존재하지 않습니다."),
    POST_INVALID_TITLE("해당 제목의 게시물이 존재하지 않습니다."),
    POST_INVALID_AUTHOR_NICKNAME("해당 닉네임의 게시물이 존재하지 않습니다."),
    POST_INVALID_CATEGORY("해당 카테고리의 게시물이 존재하지 않습니다."),

    ;

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
