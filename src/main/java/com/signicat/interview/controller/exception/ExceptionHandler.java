package com.signicat.interview.controller.exception;

import com.signicat.interview.exception.CustomTokenException;
import com.signicat.interview.exception.OperationalException;
import com.signicat.interview.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionHandler extends ResponseEntityExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(CustomTokenException.class)
    protected ResponseEntity<ExceptionResponse> handleTokenException(CustomTokenException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(LocalDateTime.now(), "Token is invalid"), HttpStatus.UNAUTHORIZED);
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(OperationalException.class)
    protected ResponseEntity<ExceptionResponse> handleOperationalException(OperationalException ex, WebRequest request) {
        return new ResponseEntity<>(new ExceptionResponse(LocalDateTime.now(), ex.getMessage()), ex.getHttpStatus());
    }

}
