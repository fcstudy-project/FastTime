package com.fasttime.domain.article.exception;

import com.fasttime.global.exception.ApplicationException;
import com.fasttime.global.exception.ErrorCode;

public class ArticleNotFoundException extends ApplicationException {
    private static final ErrorCode ERROR_CODE = ErrorCode.ARTICLE_NOT_FOUND;

    public ArticleNotFoundException() {
        super(ERROR_CODE);
    }

    public ArticleNotFoundException(String message) {
        super(ERROR_CODE, message);
    }
}
