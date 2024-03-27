package com.fasttime.domain.bootcamp.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class CertificationBadRequestException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.CERTIFICATION_BAD_REQUEST;

    public CertificationBadRequestException() {
        super(ERROR_CODE);
    }

    public CertificationBadRequestException(String message) {
        super(ERROR_CODE, message);
    }
}
