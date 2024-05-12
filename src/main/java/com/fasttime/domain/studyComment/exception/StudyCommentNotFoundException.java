package com.fasttime.domain.studyComment.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class StudyCommentNotFoundException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.STUDY_COMMENT_NOT_FOUND;

    public StudyCommentNotFoundException() {
        super(ERROR_CODE);
    }
}
