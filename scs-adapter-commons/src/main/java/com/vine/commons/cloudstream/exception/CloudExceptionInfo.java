package com.vine.commons.cloudstream.exception;

import com.vine.commons.cloudstream.MessageInfo;

/**
 * Created by vrustia on 3/28/18.
 */
public class CloudExceptionInfo implements MessageInfo {
    private CustomException exception;

    public CustomException getException() {
        return exception;
    }

    public CloudExceptionInfo setException(CustomException exception) {
        this.exception = exception;
        return this;
    }
}
