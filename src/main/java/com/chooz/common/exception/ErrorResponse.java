package com.chooz.common.exception;

public record ErrorResponse(ErrorCode errorCode, String message, String subMessage) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode, errorCode.getMessage(), errorCode.getSubMessage());
    }
}
