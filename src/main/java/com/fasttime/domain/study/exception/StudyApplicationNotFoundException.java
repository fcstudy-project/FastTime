package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class StudyApplicationNotFoundException extends ApplicationException {

    public StudyApplicationNotFoundException() {
        super(ErrorCode.STUDY_APPLICATION_NOT_FOUND);
    }
}
