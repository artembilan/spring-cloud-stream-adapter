package com.vine.commons.cloudstream.sink;

import com.vine.commons.cloudstream.adapter.CloudChannelConstants;
import com.vine.commons.cloudstream.adapter.CloudSinkChannel;
import com.vine.commons.cloudstream.config.CloudSinkConfig;
import com.vine.commons.cloudstream.config.IntegrationCloudStreamConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

import static com.vine.commons.cloudstream.adapter.CloudStreamUtil.sinkReqRep;

/**
 * Created by vrustia on 3/5/18.
 */
@SpringBootConfiguration
@Import({IntegrationCloudStreamConfig.class})
public class CloudStreamSinkConfig implements CloudSinkConfig {

    @Override
    public CloudSinkChannel[] sinks() {
        return new CloudSinkChannel[]{
                sinkReqRep(CloudChannelConstants.CLOUD_EXCEPTION_CHANNEL)
        };
    }

}
