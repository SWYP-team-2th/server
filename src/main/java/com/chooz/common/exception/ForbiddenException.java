package com.chooz.common.exception;

public class ForbiddenException extends ApplicationException {

    public ForbiddenException() {
        super(ErrorCode.FORBIDDEN);
    }
}
