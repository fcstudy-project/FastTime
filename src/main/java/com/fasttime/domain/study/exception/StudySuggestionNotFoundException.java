package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

/**
 * 스터디 참여 제안을 찾을 수 없는 경우에 대한 예외 클래스
 */
public class StudySuggestionNotFoundException extends ApplicationException {

    public StudySuggestionNotFoundException() {
        super(ErrorCode.STUDY_SUGGESTION_NOT_FOUND);
    }
}
