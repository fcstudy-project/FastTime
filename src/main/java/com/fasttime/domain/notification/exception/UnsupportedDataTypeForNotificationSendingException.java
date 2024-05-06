package com.fasttime.domain.notification.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class UnsupportedDataTypeForNotificationSendingException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.UNSUPPORTED_DATA_TYPE_FOR_NOTIFICATION_SENDING;

    public UnsupportedDataTypeForNotificationSendingException() {
        super(ERROR_CODE);
    }
}
