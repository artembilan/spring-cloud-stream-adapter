package com.vine.commons.cloudstream.adapter;

import java.util.Objects;

/**
 * Created by vrustia on 2/28/18.
 */
public class CloudSinkChannel extends CloudChannel<CloudSinkChannel> {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CloudSinkChannel)) return false;
        CloudSinkChannel that = (CloudSinkChannel) o;
        return Objects.equals(getChannel(), that.getChannel()) &&
                getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getChannel(), getType());
    }

    @Override
    public String toString() {
        return "CloudSinkChannel{" +
                "channel='" + channel + '\'' +
                ", type=" + type +
                '}';
    }
}
