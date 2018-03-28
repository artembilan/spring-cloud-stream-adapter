package com.vine.commons.cloudstream;

/**
 * Created by vrustia on 3/20/18.
 */
public class CloudMessage implements MessageInfo {
    private String message;

    public String getMessage() {
        return message;
    }

    public CloudMessage setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public String toString() {
        return "CloudMessage{" +
                "message='" + message + '\'' +
                '}';
    }
}
