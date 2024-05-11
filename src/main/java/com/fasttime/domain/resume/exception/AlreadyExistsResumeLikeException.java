package com.fasttime.domain.resume.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class AlreadyExistsResumeLikeException extends ApplicationException {
    private final static ErrorCode errorCode = ErrorCode.ALREADY_LIKE_THIS_RESUME;

    public AlreadyExistsResumeLikeException() {
        super(errorCode);
    }

    public AlreadyExistsResumeLikeException(ErrorCode errorCode,
            String message) {
        super(errorCode, message);
    }
}
