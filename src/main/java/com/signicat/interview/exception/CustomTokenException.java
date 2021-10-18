package com.signicat.interview.exception;
/**
 * This class will be handling  all checked/unchecked exception related to token.
 */
public class CustomTokenException extends RuntimeException{

    public CustomTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomTokenException(String message) {
        super(message);
    }
}
