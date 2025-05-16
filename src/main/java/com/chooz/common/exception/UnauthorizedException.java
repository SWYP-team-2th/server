package com.chooz.common.exception;

public class UnauthorizedException extends ApplicationException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
