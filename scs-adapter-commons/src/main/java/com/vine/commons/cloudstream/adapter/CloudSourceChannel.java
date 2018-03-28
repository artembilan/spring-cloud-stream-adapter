package com.vine.commons.cloudstream.adapter;

import java.util.Objects;

/**
 * Created by vrustia on 2/28/18.
 */
public class CloudSourceChannel extends CloudChannel<CloudSourceChannel> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudSourceChannel)) return false;
        CloudSourceChannel that = (CloudSourceChannel) o;
        return Objects.equals(getChannel(), that.getChannel()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {

        return Objects.hash(getChannel(), getType());
    }

    @Override
    public String toString() {
        return "CloudSourceChannel{" +
                "channel='" + channel + '\'' +
                ", type=" + type +
                '}';
    }
}
