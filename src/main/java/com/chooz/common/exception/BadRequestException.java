package com.chooz.common.exception;

public class BadRequestException extends ApplicationException {

    public BadRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
