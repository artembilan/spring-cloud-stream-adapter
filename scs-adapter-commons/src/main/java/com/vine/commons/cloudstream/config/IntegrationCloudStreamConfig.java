package com.vine.commons.cloudstream.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vine.commons.cloudstream.IntegrationCloudStreamModule;
import com.vine.commons.cloudstream.reference.IntegrationConstants;
import com.vine.commons.cloudstream.util.AnnotatedBeanLocator;
import com.vine.commons.cloudstream.util.JSONUtil;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

import static com.vine.commons.cloudstream.reference.CloudStreamConstants.CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL;

/**
 * Created by vrustia on 2/27/18.
 */
@Order
@SpringBootConfiguration
@ComponentScan(basePackageClasses = {IntegrationCloudStreamModule.class})
@EnableBinding
public class IntegrationCloudStreamConfig {

    private final AnnotatedBeanLocator annotatedBeanLocator;

    public IntegrationCloudStreamConfig(AnnotatedBeanLocator annotatedBeanLocator) {
        this.annotatedBeanLocator = annotatedBeanLocator;
    }

    @Bean(name = CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL)
    public DirectChannel cloudOutgoingReplyChannel() {
        return new DirectChannel();
    }


    /**
     * Synchronous channel used to provide integration between modules within the same process.
     *
     * @return
     */
    @Bean
    public MessageChannel inputChannel() {
        DirectChannel directChannel = new DirectChannel();
        return directChannel;
    }

    @Bean(name = IntegrationConstants.DEFAULT_REPLY_CHANNEL)
    public PublishSubscribeChannel defaultReplyChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean(name = IntegrationConstants.APP_ERROR_CHANNEL)
    public MessageChannel appErrorChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessagingTemplate owensMessagingTemplate(MessageChannel inputChannel) {
        MessagingTemplate owensMessagingTemplate = new MessagingTemplate(inputChannel);
        return owensMessagingTemplate;
    }

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(1000));
        return pollerMetadata;
    }

    @Bean
    public ObjectMapper integrationObjectMapper() {
        ObjectMapper objectMapper = JSONUtil.configureResponseSettings(new ObjectMapper());
        return objectMapper;
    }

}
