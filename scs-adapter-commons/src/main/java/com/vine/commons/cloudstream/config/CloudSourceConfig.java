package com.vine.commons.cloudstream.config;

import com.vine.commons.cloudstream.adapter.CloudSourceChannel;

/**
 * Created by vrustia on 3/4/18.
 */
public interface CloudSourceConfig {
    CloudSourceChannel[] sources();
}
