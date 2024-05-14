package com.fasttime.domain.study.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

/**
 * 스터디 참여 제안 수신자가 아닌 회원이 참여 제안을 수락하거나 거절하려고 하는 경우에 대한 예외 클래스
 */
public class NotStudySuggestionReceiverException extends ApplicationException {

    public NotStudySuggestionReceiverException() {
        super(ErrorCode.HAS_NO_PERMISSION_WITH_THIS_SUGGESTION);
    }
}
