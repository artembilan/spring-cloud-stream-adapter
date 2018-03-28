package com.vine.commons.cloudstream.sink;

import com.vine.commons.cloudstream.CloudMessage;
import com.vine.commons.cloudstream.adapter.CloudChannelConstants;
import com.vine.commons.cloudstream.exception.CustomException;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

/**
 * Created by vrustia on 3/28/18.
 */
@MessageEndpoint
public class Endpoint {

    @ServiceActivator(inputChannel = CloudChannelConstants.CLOUD_EXCEPTION_CHANNEL, requiresReply = "true")
    public CloudMessage notifyException(Message<Long> request) {
        throw new CustomException("CustomException Channel");
    }

}
