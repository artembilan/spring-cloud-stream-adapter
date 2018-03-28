package com.vine.commons.cloudstream.adapter;

import com.vine.commons.cloudstream.MessagingType;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by vrustia on 2/28/18.
 */
public class CloudStreamUtil {

    public static CloudSourceChannel[] sources(String... sourceOutputChannels) {
        return Arrays.stream(sourceOutputChannels)
                .flatMap(sourceOutputChannelName -> Stream
                        .<CloudSourceChannel>builder()
                        .add(source(sourceOutputChannelName, MessagingType.PUB_SUB))
                        .build())
                .toArray(CloudSourceChannel[]::new);
    }

    public static CloudSourceChannel source(String sourceChannel, MessagingType type) {
        return new CloudSourceChannel()
                .setChannel(sourceChannel)
                .setGroupTopic(buildGroupTopic(sourceChannel))
                .setType(type);
    }

    public static CloudSourceChannel sourceReqRep(String sourceChannel) {
        return source(sourceChannel, MessagingType.REQUEST_REPLY);
    }

    public static CloudSourceChannel sourcePubSub(String sourceChannel) {
        return source(sourceChannel, MessagingType.PUB_SUB);
    }

    public static CloudSinkChannel[] sinks(String... sinkInputChannels) {
        return Arrays.stream(sinkInputChannels)
                .flatMap(sinkInputChannelName -> Stream
                        .<CloudSinkChannel>builder()
                        .add(sink(sinkInputChannelName, MessagingType.PUB_SUB))
                        .build())
                .toArray(CloudSinkChannel[]::new);
    }

    public static CloudSinkChannel sink(String sinkChannel, MessagingType type) {
        return new CloudSinkChannel()
                .setChannel(sinkChannel)
                .setGroupTopic(buildGroupTopic(sinkChannel))
                .setType(type);
    }

    public static CloudSinkChannel sinkReqRep(String sinkChannel) {
        return sink(sinkChannel, MessagingType.REQUEST_REPLY);
    }

    public static CloudSinkChannel sinkPubSub(String sinkChannel) {
        return sink(sinkChannel, MessagingType.PUB_SUB);
    }

    public static String buildGroupTopic(String requestChannel) {
        String[] topicSegments = requestChannel.split("\\.");
        return ArrayUtils.isNotEmpty(topicSegments) ? topicSegments[0] : requestChannel;
    }

}
