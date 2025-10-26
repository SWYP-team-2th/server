package com.chooz.common.exception;

public record ErrorResponse(ErrorCode errorCode, String message) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode, errorCode.getMessage());
    }
}
