package com.fasttime.domain.certification.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class CertificationNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.CERTIFICATION_NOT_FOUND;

    public CertificationNotFoundException() {
        super(ERROR_CODE);
    }
}
