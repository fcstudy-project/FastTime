package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class StudySuggestionNotFoundException extends ApplicationException {

    public StudySuggestionNotFoundException() {
        super(ErrorCode.STUDY_SUGGESTION_NOT_FOUND);
    }
}
