package com.vine.commons.cloudstream.adapter;

import com.vine.commons.cloudstream.MessagingType;
import org.apache.commons.lang3.ArrayUtils;

/**
 * Created by vrustia on 3/6/18.
 */
public abstract class CloudChannel<T extends CloudChannel> {
    protected String groupTopic;
    protected String channel;
    protected MessagingType type;

    public String getGroupTopic() {
        return groupTopic != null && !groupTopic.isEmpty() ?
                groupTopic :
                buildGroupTopic(getChannel());
    }

    public T setGroupTopic(String groupTopic) {
        this.groupTopic = groupTopic;
        return (T) this;
    }

    public String buildGroupTopic(String requestChannel) {
        if (MessagingType.PUB_SUB.equals(getType())) {
            return requestChannel;
        } else {
            String[] topicSegments = requestChannel.split("\\.");
            return ArrayUtils.isNotEmpty(topicSegments) ? topicSegments[0] : requestChannel;
        }
    }

    public String getChannel() {
        return channel;
    }

    public T setChannel(String channel) {
        this.channel = channel;
        return (T) this;
    }

    public MessagingType getType() {
        return type;
    }

    public T setType(MessagingType type) {
        this.type = type;
        return (T) this;
    }
}
