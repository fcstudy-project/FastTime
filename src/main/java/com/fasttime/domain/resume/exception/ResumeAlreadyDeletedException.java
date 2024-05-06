package com.fasttime.domain.resume.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ResumeAlreadyDeletedException extends ApplicationException {
    private final static ErrorCode ERROR_CODE = ErrorCode.RESUME_IS_ALREADY_DELETED;

    public ResumeAlreadyDeletedException() {
        super(ERROR_CODE);
    }

    public ResumeAlreadyDeletedException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
