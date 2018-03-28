package com.vine.commons.cloudstream.adapter;


import com.vine.commons.cloudstream.MessagingType;
import com.vine.commons.cloudstream.config.CloudSourceConfig;
import com.vine.commons.cloudstream.reference.CloudStreamConstants;
import com.vine.commons.cloudstream.reference.IntegrationConstants;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.cloud.stream.binding.BindingService;
import org.springframework.cloud.stream.binding.SubscribableChannelBindingTargetFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.HeaderEnricherSpec;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

import static com.vine.commons.cloudstream.reference.CloudStreamConstants.*;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by vrustia on 2/27/18.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class CloudSourceAdapter {
    private final static Logger LOG = getLogger(CloudSourceAdapter.class);
    private static final String OUTGOING_REQUEST_CLOUD_FLOW_BEAN_PREFIX = "outgoing.flow.requestCloudMessage.";
    private static final String INCOMING_REPLY_CLOUD_FLOW_BEAN_PREFIX = "incoming.flow.replyCloudMessage.";
    private final IntegrationFlowContext integrationFlowContext;
    private final ConfigurableApplicationContext applicationContext;
    private final BindingService bindingService;
    private final SubscribableChannelBindingTargetFactory bindingTargetFactory;
    private List<CloudSourceConfig> cloudSourceConfigs;
    private ConfigurableListableBeanFactory beanFactory;

    public CloudSourceAdapter(IntegrationFlowContext integrationFlowContext, ConfigurableApplicationContext applicationContext, BindingService bindingService, SubscribableChannelBindingTargetFactory bindingTargetFactory, Optional<List<CloudSourceConfig>> cloudSourceConfigs) {
        this.integrationFlowContext = integrationFlowContext;
        this.applicationContext = applicationContext;
        this.bindingService = bindingService;
        this.bindingTargetFactory = bindingTargetFactory;
        this.beanFactory = applicationContext.getBeanFactory();
        cloudSourceConfigs.ifPresent(csf -> this.cloudSourceConfigs = csf);
    }

    @PostConstruct
    public void buildFlows() {

        Set<CloudSourceChannel> cloudSourceChannels = getAllCloudSourceChannels();

        cloudSourceChannels.forEach(cloudSourceChannel -> {

            buildOutgoingRequestCloudFlow(cloudSourceChannel);

            if (MessagingType.REQUEST_REPLY.equals(cloudSourceChannel.getType())) {
                buildIncomingReplyCloudFlow(cloudSourceChannel);
            }

        });

    }

    private Set<CloudSourceChannel> getAllCloudSourceChannels() {
        Set<CloudSourceChannel> cloudSourceChannels = new HashSet<>();
        if(cloudSourceConfigs != null) {
            for (CloudSourceConfig cloudSourceConfig : cloudSourceConfigs) {
                CloudSourceChannel[] sources = cloudSourceConfig.sources();
                if (ArrayUtils.isNotEmpty(sources)) {
                    cloudSourceChannels.addAll(Arrays.asList(sources));
                }
            }
        }
        return cloudSourceChannels;
    }

    private void buildIncomingReplyCloudFlow(CloudSourceChannel cloudSourceChannel) {
        String groupTopic = cloudSourceChannel.getGroupTopic();
        String cloudReplyChannelName = CLOUD_REPLY_PREFIX + groupTopic;

        String incomingReplyCloudChannelBeanName = CLOUD_INPUT_PREFIX + cloudReplyChannelName;

        if (!beanFactory.containsBean(incomingReplyCloudChannelBeanName)) {
            SubscribableChannel input = createCloudInputReplyBinding(cloudReplyChannelName);

            createIntegrationFlowFromCloudInputReplyToLocal(cloudReplyChannelName, input);
        }
    }

    private SubscribableChannel createCloudInputReplyBinding(String cloudReplyChannelName) {
        String incomingReplyCloudChannelBeanName = CLOUD_INPUT_PREFIX + cloudReplyChannelName;
        if (!beanFactory.containsBean(incomingReplyCloudChannelBeanName)) {
            SubscribableChannel input = bindingTargetFactory.createInput(cloudReplyChannelName);

            input = (SubscribableChannel) beanFactory.initializeBean(input, incomingReplyCloudChannelBeanName);
            beanFactory.registerSingleton(incomingReplyCloudChannelBeanName, input);

            bindingService.bindConsumer(input, cloudReplyChannelName);
        }

        return (SubscribableChannel) beanFactory.getBean(incomingReplyCloudChannelBeanName);
    }

    private void createIntegrationFlowFromCloudInputReplyToLocal(String cloudReplyChannelName, SubscribableChannel input) {
        String applicationName = applicationContext.getEnvironment().getProperty(SPRING_APPLICATION_NAME);
        StandardIntegrationFlow replyCloudMessage = IntegrationFlows
                .from(input)
                .filter(Message.class, m -> applicationName.equals(m.getHeaders().get(SOURCE_APP_HEADER)))
                .routeToRecipients(r -> r
                        .recipientFlow(m -> "true".equals(m.getHeaders().get(EXCEPTION_HINT_HEADER)), flow -> flow
                                .channel(CloudStreamConstants.CLOUD_ERROR_CHANNEL))
                        .recipient(IntegrationConstants.DEFAULT_REPLY_CHANNEL, m -> StringUtils.isEmpty((String) m.getHeaders().get(EXCEPTION_HINT_HEADER))))
                .get();

        String integrationFlowBeanName = INCOMING_REPLY_CLOUD_FLOW_BEAN_PREFIX + cloudReplyChannelName;
        integrationFlowContext.registration(replyCloudMessage).id(integrationFlowBeanName).register();
    }

    private void buildOutgoingRequestCloudFlow(CloudSourceChannel cloudSourceChannel) {

        createLocalRequestChannel(cloudSourceChannel);

        String groupTopic = cloudSourceChannel.getGroupTopic();
        String cloudRequestChannelName = CLOUD_REQUEST_PREFIX + groupTopic;

        String outgoingRequestCloudBeanName = CLOUD_OUTPUT_PREFIX + cloudRequestChannelName;

        if (!beanFactory.containsBean(outgoingRequestCloudBeanName)) {
            createOutputBinding(cloudRequestChannelName, outgoingRequestCloudBeanName);
        }

        SubscribableChannel outputBindingChannel = (SubscribableChannel) beanFactory.getBean(outgoingRequestCloudBeanName);

        createIntegrationFlowFromLocalToCloudOutput(cloudSourceChannel, outputBindingChannel, cloudRequestChannelName);

    }

    private void createLocalRequestChannel(CloudSourceChannel cloudSourceChannel) {
        String requestChannel = cloudSourceChannel.getChannel();
        if (!beanFactory.containsBean(requestChannel)) {
            DirectChannel outgoingLocalRequestChannel = new DirectChannel();
            outgoingLocalRequestChannel.setBeanName(requestChannel);

            beanFactory.initializeBean(outgoingLocalRequestChannel, requestChannel);
            beanFactory.registerSingleton(requestChannel, outgoingLocalRequestChannel);
        }
    }

    private SubscribableChannel createOutputBinding(String cloudRequestChannelName, String outgoingRequestCloudBeanName) {
        SubscribableChannel output = bindingTargetFactory.createOutput(cloudRequestChannelName);

        output = (SubscribableChannel) beanFactory.initializeBean(output, outgoingRequestCloudBeanName);

        beanFactory.registerSingleton(outgoingRequestCloudBeanName, output);

        bindingService.bindProducer(output, cloudRequestChannelName);

        return (SubscribableChannel) beanFactory.getBean(outgoingRequestCloudBeanName);
    }

    private void createIntegrationFlowFromLocalToCloudOutput(CloudSourceChannel cloudSourceChannel, SubscribableChannel outputBinding, String cloudRequestChannelName) {

        String requestChannel = cloudSourceChannel.getChannel();
        MessageChannel localRequestChannel = (MessageChannel) beanFactory.getBean(requestChannel);

        StandardIntegrationFlow outgoingCloudMessage = IntegrationFlows.from(localRequestChannel)
                .enrichHeaders(headerEnricherSpec -> {
                    headerEnricherSpec
                            .header(LOCAL_TOPIC_HEADER, requestChannel, true)
                            .header(SOURCE_APP_HEADER, applicationContext.getEnvironment().getProperty(SPRING_APPLICATION_NAME), true);
                })
                .enrichHeaders(HeaderEnricherSpec::headerChannelsToString)
                .channel(outputBinding)
                .get();

        String integrationOutgoingCloudFlow = OUTGOING_REQUEST_CLOUD_FLOW_BEAN_PREFIX + requestChannel;
        integrationFlowContext.registration(outgoingCloudMessage).id(integrationOutgoingCloudFlow).register();
    }

}
