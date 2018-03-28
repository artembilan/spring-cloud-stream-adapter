package com.vine.commons.cloudstream.reference;

import org.springframework.kafka.support.KafkaHeaders;

/**
 * Created by vrustia on 2/28/18.
 */
public interface CloudStreamConstants {

    String CLOUD_REQUEST_PREFIX = "cloud.request.";
    String CLOUD_REPLY_PREFIX = "cloud.reply.";

    String CLOUD_INPUT_PREFIX = "input.";
    String CLOUD_OUTPUT_PREFIX = "output.";

    String CLOUD_REPLY_CHANNEL_HEADER_KEY = "cloud.replyChannel";
    String CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL = "outgoing.default-reply-channel";

    String CLOUD_ERROR_CHANNEL_HEADER_KEY = "cloud.errorChannel";
    String CLOUD_ERROR_CHANNEL = "cloudErrorChannel";
    String CLOUD_ERROR_TRANSFORMER_CHANNEL = "cloudErrorTransformerChannel";

    String OWENS_CLOUD_RECEIVED_TOPIC_HEADER = KafkaHeaders.RECEIVED_TOPIC;
    String SPRING_APPLICATION_NAME = "spring.application.name";
    String LOCAL_TOPIC_HEADER = "localTopic";
    String SOURCE_APP_HEADER = "sourceApp";
    String CONSUMER_ERROR_CHANNEL = "errorChannel";
    String EXCEPTION_HINT_HEADER = "exception";
}
