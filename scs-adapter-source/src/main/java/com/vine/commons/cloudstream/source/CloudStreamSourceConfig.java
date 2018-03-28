package com.vine.commons.cloudstream.source;

import com.vine.commons.cloudstream.adapter.CloudChannelConstants;
import com.vine.commons.cloudstream.adapter.CloudSourceChannel;
import com.vine.commons.cloudstream.config.CloudSourceConfig;
import com.vine.commons.cloudstream.config.IntegrationCloudStreamConfig;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Import;

import static com.vine.commons.cloudstream.adapter.CloudStreamUtil.sourceReqRep;

/**
 * Created by vrustia on 3/5/18.
 */
@SpringBootConfiguration
@Import({IntegrationCloudStreamConfig.class})
public class CloudStreamSourceConfig implements CloudSourceConfig {

    @Override
    public CloudSourceChannel[] sources() {
        return new CloudSourceChannel[]{
                sourceReqRep(CloudChannelConstants.CLOUD_EXCEPTION_CHANNEL)
        };
    }

}
