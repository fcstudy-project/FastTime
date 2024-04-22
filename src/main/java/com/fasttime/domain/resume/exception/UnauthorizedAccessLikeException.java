package com.fasttime.domain.resume.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UnauthorizedAccessLikeException extends ApplicationException {

    private final static ErrorCode errorCode = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_LIKE;

    public UnauthorizedAccessLikeException() {
        super(errorCode);
    }

    public UnauthorizedAccessLikeException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
