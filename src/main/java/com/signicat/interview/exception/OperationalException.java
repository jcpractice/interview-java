package com.signicat.interview.exception;

import org.springframework.http.HttpStatus;

/**
 * This exception class will be handling all checked/unchecked
 * Operational exceptions related to business logic.
 */
public class OperationalException extends RuntimeException{
    private HttpStatus httpStatus;
    public OperationalException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public OperationalException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
