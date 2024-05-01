package com.fasttime.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    //EMAIL
    EMAIL_SENDING_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다."),
    EMAIL_TEMPLATE_LOAD_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 템플릿 로드에 실패했습니다."),
    EMAIL_VERIFICATION_CODE_EXPIRED(HttpStatus.GONE, "인증 코드의 유효 기간이 만료되었습니다."),

    // MEMBER
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    MEMBER_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 가입된 회원입니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임 입니다."),
    MEMBER_NOT_MATCH_INFO(HttpStatus.BAD_REQUEST, "아이디 또는 비밀번호가 일치하지 않습니다."),
    MEMBER_NOT_MATCH_RE_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호 재확인이 일치하지 않습니다."),
    MEMBER_SOFT_DELETED(HttpStatus.NOT_FOUND, "이미 탈퇴한 회원입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token 이 유효하지 않습니다."),
    AUTHENTICATION_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Security Context에 인증 정보가 없습니다."),
    LOGGED_OUT(HttpStatus.UNAUTHORIZED, "로그아웃 된 회원 입니다."),
    UNMATCHED_MEMBER(HttpStatus.UNAUTHORIZED, "토큰의 회원 정보가 일치하지 않습니다."),

    // ADMIN
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다"),

    // ARTICLE
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    BAD_REPORT_STATUS(HttpStatus.BAD_REQUEST, "신고 후처리를 할 수 없습니다."),
    REPORT_ACCEPTED_ARTICLE(HttpStatus.UNAUTHORIZED, "신고 처리된 게시글입니다."),
    HAS_NO_PERMISSION_WITH_THIS_ARTICLE(HttpStatus.UNAUTHORIZED, "해당 게시글에 대한 권한이 없습니다."),

    // COMMENT
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    HAS_NO_PERMISSION_WITH_THIS_COMMENT(HttpStatus.UNAUTHORIZED, "댓글 작성자만 해당 댓글 수정/삭제가 가능합니다."),
    CANNOT_MULTIPLE_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "다중 조건 검색은 불가합니다."),

    // RECORD
    RECORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 좋아요/싫어요 입니다."),
    CANNOT_RECORD_BOTH_IN_ONE_POST(HttpStatus.BAD_REQUEST, "한 게시글에 좋아요와 싫어요를 모두 등록할 수는 없습니다."),
    DUPLICATED_REQUEST_FOR_RECORD(HttpStatus.BAD_REQUEST, "중복된 좋아요/싫어요 등록 요청입니다."),

    // REPORT
    ALREADY_REPORTED_THIS_POST(HttpStatus.BAD_REQUEST, "이미 신고한 게시글입니다."),

    // REVIEW
    TAG_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 태그입니다."),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리뷰입니다."),
    BOOTCAMP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 부트캠프입니다."),
    ALREADY_DELETED_THIS_REVIEW(HttpStatus.BAD_REQUEST, "이미 삭제한 리뷰입니다."),
    HAS_NO_PERMISSION_WITH_THIS_REVIEW(HttpStatus.UNAUTHORIZED, "리뷰 작성/삭제 권한이 없습니다."),
    REVIEW_ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 리뷰를 작성했습니다."),


    // RESUME
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 자기소개서입니다."),
    RESUME_IS_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제한 자기소개서입니다."),
    HAS_NO_PERMISSION_WITH_THIS_RESUME(HttpStatus.UNAUTHORIZED, "해당 자기소개서에 대한 권한이 없습니다."),

    // REFERENCE
    ACTIVITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 대외활동입니다."),
    COMPETITION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 공모전입니다."),

    // CERTIFICATION
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 인증입니다."),
    UNAUTHORIZED_CERTIFICATION(HttpStatus.UNAUTHORIZED, "인증 권한이 없습니다."),
    CERTIFICATION_BAD_REQUEST(HttpStatus.BAD_REQUEST, "이미 처리한 인증입니다."),

    // STUDY
    STUDY_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 스터디게시판입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND,"존재하지 않는 스터디 분야입니다."),
    HAS_NO_PERMISSION_WITH_THIS_STUDY(HttpStatus.UNAUTHORIZED, "해당 스터디 게시글에 대한 권한이 없습니다."),
    STUDY_DELETED(HttpStatus.NOT_FOUND,"삭제된 스터디 모집글입니다."),
    STUDY_APPLICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 스터디 신청입니다."),
    STUDY_SUGGESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 스터디 제안입니다."),

    // SSE & NOTIFICATION
    SSE_CONNECTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SSE 연결에 실패했습니다."),
    UNSUPPORTED_DATA_TYPE_FOR_NOTIFICATION_SENDING(HttpStatus.INTERNAL_SERVER_ERROR,
        "알림 전송 기능을 사용할 메서드의 반환값이 알림 전송을 지원하지 않는 자료형입니다."),


    // 5xx
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 에러");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
