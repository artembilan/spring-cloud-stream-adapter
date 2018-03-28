package com.vine.commons.cloudstream.config;

import com.vine.commons.cloudstream.adapter.CloudSinkChannel;

/**
 * Created by vrustia on 3/4/18.
 */
public interface CloudSinkConfig {
    CloudSinkChannel[] sinks();
}
