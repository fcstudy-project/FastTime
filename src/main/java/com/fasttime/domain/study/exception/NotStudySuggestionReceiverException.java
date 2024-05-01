package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotStudySuggestionReceiverException extends ApplicationException {

    public NotStudySuggestionReceiverException() {
        super(ErrorCode.HAS_NO_PERMISSION_WITH_THIS_SUGGESTION);
    }
}
