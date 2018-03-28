package com.vine.commons.cloudstream.source;

import com.vine.commons.cloudstream.CloudMessage;
import com.vine.commons.cloudstream.adapter.CloudChannelConstants;
import com.vine.commons.cloudstream.reference.IntegrationConstants;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(errorChannel = IntegrationConstants.APP_ERROR_CHANNEL,
        defaultReplyChannel = IntegrationConstants.DEFAULT_REPLY_CHANNEL,
        defaultRequestTimeout = IntegrationConstants.DEFAULT_REQUEST_TIMEOUT,
        defaultReplyTimeout = IntegrationConstants.DEFAULT_REPLY_TIMEOUT)
public interface TopicMessageGateway {

    @Gateway(requestChannel = CloudChannelConstants.CLOUD_EXCEPTION_CHANNEL)
    CloudMessage exception(Long id);

}
