package com.vine.commons.cloudstream.exception;

/**
 * Created by vrustia on 3/28/18.
 */
public class CustomException extends RuntimeException {
    private String message;

    public CustomException() {
    }

    public CustomException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public CustomException setMessage(String message) {
        this.message = message;
        return this;
    }
}
