package com.chooz.common.exception;

public class InternalServerException extends ApplicationException {

    public InternalServerException() {
        super(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(Exception e) {
        super(e, ErrorCode.INTERNAL_SERVER_ERROR);
    }

    public InternalServerException(ErrorCode errorCode) {
        super(errorCode);
    }
}
