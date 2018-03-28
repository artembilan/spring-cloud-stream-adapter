package com.vine.commons.cloudstream.annotation;

import com.vine.commons.cloudstream.reference.IntegrationConstants;
import org.springframework.core.annotation.AliasFor;
import org.springframework.integration.annotation.AnnotationConstants;
import org.springframework.integration.annotation.GatewayHeader;
import org.springframework.integration.annotation.MessagingGateway;

import java.lang.annotation.*;

/**
 * @author vrustia - 12/3/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@MessagingGateway
public @interface OwensMessageGateway {

    @AliasFor(annotation = MessagingGateway.class, attribute = "errorChannel")
    String errorChannel() default IntegrationConstants.APP_ERROR_CHANNEL;

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "name")
    String name() default "";

    /**
     * Identifies default channel the messages will be sent to upon invocation of methods of the gateway proxy.
     *
     * @return the suggested channel name, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultRequestChannel")
    String defaultRequestChannel() default "";

    /**
     * Identifies default channel the gateway proxy will subscribe to to receive reply {@code Message}s, which will then be
     * converted to the return type of the method signature.
     *
     * @return the suggested channel name, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultReplyChannel")
    String defaultReplyChannel() default IntegrationConstants.DEFAULT_REPLY_CHANNEL;

    /**
     * Provides the amount of time dispatcher would wait to send a {@code Message}.
     * This timeout would only apply if there is a potential to block in the send call.
     * For example if this gateway is hooked up to a {@code QueueChannel}. 
     *
     * @return the suggested timeout in milliseconds, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultRequestTimeout")
    String defaultRequestTimeout() default IntegrationConstants.DEFAULT_REQUEST_TIMEOUT;

    /**
     * Allows to specify how long this gateway will wait for the reply {@code Message}
     * before returning. By default it will wait indefinitely. {@code null} is returned
     * if the gateway times out.
     *
     * @return the suggested timeout in milliseconds, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultReplyTimeout")
    String defaultReplyTimeout() default IntegrationConstants.DEFAULT_REPLY_TIMEOUT;

    /**
     * Provide a reference to an implementation of {@link java.util.concurrent.Executor}
     * to use for any of the interface methods that have a {@link java.util.concurrent.Future} return type.
     * This {@code Executor} will only be used for those async methods; the sync methods
     * will be invoked in the caller's thread.
     * Use {@link AnnotationConstants#NULL} to specify no async executor - for example
     * if your downstream flow returns a {@link java.util.concurrent.Future}.
     *
     * @return the suggested executor bean name, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "asyncExecutor")
    String asyncExecutor() default "";

    /**
     * An expression that will be used to generate the {@code payload} for all methods in the service interface
     * unless explicitly overridden by a method declaration. Variables include {@code #args}, {@code #methodName},
     * {@code #methodString} and {@code #methodObject};
     * a bean resolver is also available, enabling expressions like {@code @someBean(#args)}.
     *
     * @return the suggested payload expression, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultPayloadExpression")
    String defaultPayloadExpression() default "";

    /**
     * Provides custom message headers. These default headers are created for
     * all methods on the service-interface (unless overridden by a specific method).
     *
     * @return the suggested payload expression, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "defaultHeaders")
    GatewayHeader[] defaultHeaders() default {};

    /**
     * An {@link org.springframework.integration.gateway.MethodArgsMessageMapper}
     * to map the method arguments to a {@link org.springframework.messaging.Message}. When this
     * is provided, no {@code payload-expression}s or {@code header}s are allowed; the custom mapper is
     * responsible for creating the message.
     *
     * @return the suggested mapper bean name, if any
     */
    @AliasFor(annotation = MessagingGateway.class, attribute = "mapper")
    String mapper() default "";
}
