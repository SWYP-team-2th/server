package com.chooz.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import javax.naming.AuthenticationException;
import java.util.Arrays;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationControllerAdvice {

    private final DiscordMessageSender discordMessageSender;
    private final Environment environment;

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handle(BadRequestException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return ResponseEntity.badRequest()
                .body(response);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedException e) {
        ErrorResponse response = new ErrorResponse(e.getErrorCode());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class,
            HandlerMethodValidationException.class
    })
    public ResponseEntity<ErrorResponse> invalidArgument(Exception e) {
        log.debug("invalidArgument: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(ErrorCode.INVALID_ARGUMENT));
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            MethodArgumentTypeMismatchException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ErrorResponse> notFound(Exception e) {
        log.debug("notFound: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handle(AuthenticationException e) {
        log.debug(e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(ErrorCode.INVALID_TOKEN));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handle(ForbiddenException e) {
        log.debug(e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(ErrorCode.FORBIDDEN));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception e, WebRequest webRequest) {
        log.error("Exception", e);
        if (!Arrays.asList(environment.getActiveProfiles()).contains("local")) {
            discordMessageSender.sendDiscordAlarm(e, webRequest);
        }
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
