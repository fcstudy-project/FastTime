package com.fasttime.domain.member.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class VerificationCodeExpiredException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.EMAIL_VERIFICATION_CODE_EXPIRED;

    public VerificationCodeExpiredException() {

        super(ERROR_CODE);
    }
}
