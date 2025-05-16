package com.chooz.common.exception;

public class ServiceUnavailableException extends ApplicationException {

    public ServiceUnavailableException(ErrorCode errorCode) {
        super(errorCode);
    }
}
