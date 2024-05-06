package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class StudyDeleteException extends ApplicationException {
    public StudyDeleteException() {
        super(ErrorCode.STUDY_DELETED);
    }
}
