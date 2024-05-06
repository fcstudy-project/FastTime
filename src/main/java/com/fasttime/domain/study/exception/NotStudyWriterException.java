package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotStudyWriterException extends ApplicationException {

    public NotStudyWriterException() {
        super(ErrorCode.HAS_NO_PERMISSION_WITH_THIS_STUDY);
    }

    public NotStudyWriterException(String message) {
        super(ErrorCode.HAS_NO_PERMISSION_WITH_THIS_STUDY, message);
    }
}
