package com.fasttime.domain.studyComment.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class NotStudyCommentAuthorException extends ApplicationException {

    private static final ErrorCode ERROR_CODE = ErrorCode.HAS_NO_PERMISSION_WITH_THIS_STUDY_COMMENT;

    public NotStudyCommentAuthorException() {
        super(ERROR_CODE);
    }
}
