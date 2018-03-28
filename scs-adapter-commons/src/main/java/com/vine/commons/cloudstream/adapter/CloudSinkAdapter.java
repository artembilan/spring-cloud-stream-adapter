package com.vine.commons.cloudstream.adapter;

import com.vine.commons.cloudstream.MessagingType;
import com.vine.commons.cloudstream.config.CloudSinkConfig;
import com.vine.commons.cloudstream.reference.CloudStreamConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.binding.SubscribableChannelBindingTargetFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.vine.commons.cloudstream.reference.CloudStreamConstants.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by vrustia on 2/28/18.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CloudSinkAdapter {
    private final static Logger LOG = getLogger(CloudSinkAdapter.class);
    private static final String OUTGOING_REPLY_CLOUD_FLOW_BEAN = "outgoing.flow.replyCloudMessage";
    private static final String INCOMING_REQUEST_CLOUD_FLOW_BEAN_PREFIX = "incoming.flow.requestCloudMessage.";
    private final IntegrationFlowContext integrationFlowContext;
    private final ConfigurableApplicationContext applicationContext;
    private final BindingService bindingService;
    private final SubscribableChannelBindingTargetFactory bindingTargetFactory;
    private List<CloudSinkConfig> cloudSinkConfigs;
    private ConfigurableListableBeanFactory beanFactory;

    public CloudSinkAdapter(IntegrationFlowContext integrationFlowContext, ConfigurableApplicationContext applicationContext, BindingService bindingService, SubscribableChannelBindingTargetFactory bindingTargetFactory, Optional<List<CloudSinkConfig>> cloudSinkConfigs) {
        this.integrationFlowContext = integrationFlowContext;
        this.applicationContext = applicationContext;
        this.bindingService = bindingService;
        this.bindingTargetFactory = bindingTargetFactory;
        this.beanFactory = applicationContext.getBeanFactory();
        cloudSinkConfigs.ifPresent(csf -> this.cloudSinkConfigs = csf);
    }

    @PostConstruct
    public void buildFlows() {

        Set<CloudSinkChannel> cloudSinkChannels = getAllCloudSinkChannels();

        cloudSinkChannels
                .forEach(cloudSinkChannel -> {

                    buildIncomingRequestCloudFlow(cloudSinkChannel);

                    if (MessagingType.REQUEST_REPLY.equals(cloudSinkChannel.getType())) {
                        buildOutgoingReplyCloudFlow(cloudSinkChannel);
                    }
                });

        buildOutgoingReplyRouter();
    }

    private Set<CloudSinkChannel> getAllCloudSinkChannels() {
        Set<CloudSinkChannel> cloudSinkChannels = new HashSet<>();
        if (cloudSinkConfigs != null) {
            for (CloudSinkConfig cloudSinkConfig : cloudSinkConfigs) {
                CloudSinkChannel[] sinks = cloudSinkConfig.sinks();
                if (ArrayUtils.isNotEmpty(sinks)) {
                    cloudSinkChannels.addAll(Arrays.asList(sinks));
                }
            }
        }
        return cloudSinkChannels;
    }

    private void buildOutgoingReplyRouter() {
        StandardIntegrationFlow outgoingReplyCloudFlow = IntegrationFlows
                .from(CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL)
                .enrichHeaders(headerEnricherSpec -> {
                    headerEnricherSpec
                            .defaultOverwrite(true)
                            .headerFunction(MessageHeaders.REPLY_CHANNEL, message -> {
                                String replyChannel = (String) message.getHeaders().get(CLOUD_REPLY_CHANNEL_HEADER_KEY);
                                return replyChannel;
                            }, true);
                })
                .route(new AbstractMessageRouter() {
                    @Override
                    protected Collection<MessageChannel> determineTargetChannels(Message<?> message) {

                        String cloudTopicName = (String) message.getHeaders().get(OWENS_CLOUD_RECEIVED_TOPIC_HEADER);

                        String groupTopicChannelName = cloudTopicName.replace(CloudStreamConstants.CLOUD_REQUEST_PREFIX, "");

                        String cloudOutgoingReplyChannelName = CLOUD_OUTPUT_PREFIX + CLOUD_REPLY_PREFIX + groupTopicChannelName;

                        List<MessageChannel> list = new ArrayList<>();
                        list.add((MessageChannel) beanFactory.getBean(cloudOutgoingReplyChannelName));
                        return list;
                    }
                })
                .get();

        String integrationOutgoingReplyCloudFlowBeanName = OUTGOING_REPLY_CLOUD_FLOW_BEAN;
        integrationFlowContext.registration(outgoingReplyCloudFlow).id(integrationOutgoingReplyCloudFlowBeanName).register();
    }

    private void buildOutgoingReplyCloudFlow(CloudSinkChannel cloudSinkChannel) {
        String groupTopic = cloudSinkChannel.getGroupTopic();
        String cloudOutgoingReplyChannelName = CLOUD_REPLY_PREFIX + groupTopic;

        String outgoingReplyCloudBeanName = CLOUD_OUTPUT_PREFIX + cloudOutgoingReplyChannelName;
        if (!beanFactory.containsBean(outgoingReplyCloudBeanName)) {
            final SubscribableChannel output = (SubscribableChannel) beanFactory.initializeBean(bindingTargetFactory.createOutput(cloudOutgoingReplyChannelName), outgoingReplyCloudBeanName);
            beanFactory.registerSingleton(outgoingReplyCloudBeanName, output);

            bindingService.bindProducer(output, cloudOutgoingReplyChannelName);
        }
    }

    private void buildIncomingRequestCloudFlow(CloudSinkChannel cloudSinkChannel) {

        createLocalRequestChannel(cloudSinkChannel);

        String groupTopic = cloudSinkChannel.getGroupTopic();
        String cloudRequestChannelName = CLOUD_REQUEST_PREFIX + groupTopic;
        String incomingRequestCloudBeanName = CLOUD_INPUT_PREFIX + cloudRequestChannelName;
        if (!beanFactory.containsBean(incomingRequestCloudBeanName)) {
            createCloudInputRequestBinding(cloudRequestChannelName);
        }

        SubscribableChannel input = (SubscribableChannel) beanFactory.getBean(incomingRequestCloudBeanName);
        createIntegrationFlowFromCloudInputRequestToLocal(cloudSinkChannel, cloudRequestChannelName, input);
    }

    /**
     * To make sure the right channel implementation is created depending on MessagingType.
     *
     * @param cloudSinkChannel
     * @return
     */
    private MessageChannel createLocalRequestChannel(CloudSinkChannel cloudSinkChannel) {

        String requestChannel = cloudSinkChannel.getChannel();

        if (!beanFactory.containsBean(requestChannel)) {

            AbstractMessageChannel localRequestChannel = MessagingType.REQUEST_REPLY.equals(cloudSinkChannel.getType()) ?
                    new DirectChannel() :
                    new PublishSubscribeChannel();

            localRequestChannel.setBeanName(requestChannel);

            beanFactory.initializeBean(localRequestChannel, requestChannel);
            beanFactory.registerSingleton(requestChannel, localRequestChannel);
        }

        return (MessageChannel) beanFactory.getBean(requestChannel);
    }

    private SubscribableChannel createCloudInputRequestBinding(String cloudRequestChannelName) {
        String incomingRequestCloudBeanName = CLOUD_INPUT_PREFIX + cloudRequestChannelName;
        SubscribableChannel input = bindingTargetFactory.createInput(cloudRequestChannelName);

        input = (SubscribableChannel) beanFactory.initializeBean(input, incomingRequestCloudBeanName);
        beanFactory.registerSingleton(incomingRequestCloudBeanName, input);

        bindingService.bindConsumer(input, cloudRequestChannelName);

        return (SubscribableChannel) beanFactory.getBean(incomingRequestCloudBeanName);
    }

    private void createIntegrationFlowFromCloudInputRequestToLocal(CloudSinkChannel cloudSinkChannel, String cloudRequestChannelName, SubscribableChannel cloudInputBinding) {
        String integrationIncomingRequestCloudFlowBeanName = INCOMING_REQUEST_CLOUD_FLOW_BEAN_PREFIX + cloudRequestChannelName + "." + cloudSinkChannel.getType();

        if (!beanFactory.containsBean(integrationIncomingRequestCloudFlowBeanName)) {
            IntegrationFlowBuilder incomingRequestFlowBuilder = IntegrationFlows.from(cloudInputBinding);
            if (MessagingType.REQUEST_REPLY.equals(cloudSinkChannel.getType())) {
                incomingRequestFlowBuilder = incomingRequestFlowBuilder
                        .enrichHeaders(headerEnricherSpec -> {
                            headerEnricherSpec
                                    .defaultOverwrite(true);
                            headerEnricherSpec.headerFunction(CloudStreamConstants.CLOUD_REPLY_CHANNEL_HEADER_KEY, message -> {
                                headerEnricherSpec.defaultOverwrite(true);
                                String replyChannel = (String) message.getHeaders().getReplyChannel();
                                return replyChannel;
                            }, true);
                            headerEnricherSpec.headerFunction(MessageHeaders.REPLY_CHANNEL, message -> {
                                return CloudStreamConstants.CLOUD_OUTGOING_DEFAULT_REPLY_CHANNEL;
                            }, true);
                        });
            }

            StandardIntegrationFlow incomingRequestCloudFlow = incomingRequestFlowBuilder
                    .route("headers['" + CloudStreamConstants.LOCAL_TOPIC_HEADER + "']")
                    .get();

            integrationFlowContext.registration(incomingRequestCloudFlow).id(integrationIncomingRequestCloudFlowBeanName).register();
        }

    }

}
