package com.fasttime.domain.bootcamp.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class CertificationUnAuthException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.UNAUTHORIZED_CERTIFICATION_WITHDRAWAL;

    public CertificationUnAuthException() {
        super(ERROR_CODE);
    }
}
