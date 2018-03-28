package com.vine.commons.cloudstream.endpoint;

import com.vine.commons.cloudstream.reference.IntegrationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.handler.ReplyRequiredException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

/**
 * @author vrustia - 12/3/15.
 */
@MessageEndpoint
public class AppErrorEndpoint {

    private final static Logger LOG = LoggerFactory.getLogger(AppErrorEndpoint.class);

    private static final Message<Object> NULL_MESSAGE = createNullMessage();

    private static Message<Object> createNullMessage() {
        return new Message<Object>() {
            @Override
            public Object getPayload() {
                return null;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        };
    }

    @ServiceActivator(inputChannel = IntegrationConstants.APP_ERROR_CHANNEL, requiresReply = "true")
    public Message<?> errorHandler(Message<MessagingException> message) {

        Object payload = message.getPayload();

        if (payload instanceof ReplyRequiredException) {
            return NULL_MESSAGE;
        }

        if (payload instanceof MessagingException) {
            handleMessagingException((MessagingException) payload);
        }

        return message;
    }

    private void handleMessagingException(MessagingException e) {
        Throwable rootCause = e.getCause();
        LOG.error("Exception received after sending to endpoint={} ", IntegrationConstants.APP_ERROR_CHANNEL, rootCause);
        if (rootCause instanceof RuntimeException)
            throw (RuntimeException) rootCause;
        else throw new RuntimeException(e);
    }

    @Transformer(inputChannel = IntegrationConstants.APP_ERROR_CHANNEL)
    public Message<?> transformNull(Message<Object> message) {
        Object payload = message.getPayload();

        if (payload instanceof ReplyRequiredException) {
            return NULL_MESSAGE;
        }

        return message;
    }

}