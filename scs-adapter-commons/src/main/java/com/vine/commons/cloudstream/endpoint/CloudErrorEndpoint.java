package com.vine.commons.cloudstream.endpoint;


import com.vine.commons.cloudstream.exception.CloudExceptionInfo;
import com.vine.commons.cloudstream.exception.CustomException;
import com.vine.commons.cloudstream.reference.CloudStreamConstants;
import com.vine.commons.cloudstream.reference.IntegrationConstants;
import com.vine.commons.cloudstream.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.handler.ReplyRequiredException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.MessagingException;

/**
 * @author vrustia - 12/3/15.
 */
@MessageEndpoint
public class CloudErrorEndpoint {

    private final static Logger LOG = LoggerFactory.getLogger(CloudErrorEndpoint.class);

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

    @ServiceActivator(inputChannel = CloudStreamConstants.CLOUD_ERROR_CHANNEL, outputChannel = IntegrationConstants.APP_ERROR_CHANNEL)
    public Message<?> errorHandler(Message<String> message) {
        CloudExceptionInfo exceptionInfo = JSONUtil.convert(message.getPayload(), CloudExceptionInfo.class);
        CustomException owensBaseException = exceptionInfo.getException();

        MessagingException messagingException = new MessageHandlingException(message, owensBaseException);
        return MessageBuilder.withPayload(messagingException).copyHeaders(message.getHeaders()).build();
    }

    @ServiceActivator(inputChannel = CloudStreamConstants.CONSUMER_ERROR_CHANNEL)
    public Message<?> globalErrorChannel(Message message) {
        return buildExceptionMessage(message);
    }

    private Message<?> buildExceptionMessage(Message message) {
        MessagingException messagingException = (MessagingException) message.getPayload();
        Message originalFailedMessage = messagingException.getFailedMessage();

        CustomException owensException = null;

        RuntimeException exceptionCause = (RuntimeException) messagingException.getCause();
        if (exceptionCause instanceof CustomException) {
            owensException = (CustomException) exceptionCause;
        } else if (exceptionCause instanceof MessageHandlingException) {
            owensException = (CustomException) exceptionCause.getCause();
        }

        return MessageBuilder
                .withPayload(new CloudExceptionInfo()
                        .setException(owensException))
                .copyHeaders(originalFailedMessage.getHeaders())
                .setHeader(CloudStreamConstants.CLOUD_REPLY_CHANNEL_HEADER_KEY, originalFailedMessage.getHeaders().getReplyChannel())
                .setHeader(MessageHeaders.REPLY_CHANNEL, CloudStreamConstants.CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL)
                .build();
    }

    @Transformer(inputChannel = CloudStreamConstants.CONSUMER_ERROR_CHANNEL, outputChannel = CloudStreamConstants.CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL)
    public Message<?> transformer(Message message) {
        Object payload = message.getPayload();

        if (payload instanceof ReplyRequiredException) {
            return NULL_MESSAGE;
        } else if (payload instanceof MessagingException) {
            return buildExceptionMessage(message);
        }

        return message;
    }

}
