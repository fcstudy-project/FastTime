package com.fasttime.domain.notification.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class SseConnectionFailedException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.SSE_CONNECTION_FAILED;

    public SseConnectionFailedException() {
        super(ERROR_CODE);
    }
}
