package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class StudyNotFoundException extends ApplicationException {

    public StudyNotFoundException() {
        super(ErrorCode.STUDY_NOT_FOUND);
    }
}
