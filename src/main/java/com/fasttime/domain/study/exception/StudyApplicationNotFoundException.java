package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

/**
 * 스터디 참여 신청을 찾을 수 없는 경우에 대한 예외 클래스
 */
public class StudyApplicationNotFoundException extends ApplicationException {

    public StudyApplicationNotFoundException() {
        super(ErrorCode.STUDY_APPLICATION_NOT_FOUND);
    }
}
