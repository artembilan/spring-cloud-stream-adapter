package com.vine.commons.cloudstream.annotation;

import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;

import java.lang.annotation.*;

/**
 * @author vrustia - 12/3/15.
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@ServiceActivator
public @interface OwensErrorChannelHandler {

    String inputChannel() default "";

    String outputChannel() default "";

    /**
     * Specify whether the service method must return a non-null value. This value is
     * {@code false} by default, but if set to {@code true}, a
     * {@link org.springframework.integration.handler.ReplyRequiredException} will is thrown when
     * the underlying service method (or expression) returns a null value.
     * Can be specified as 'property placeholder', e.g. {@code ${spring.integration.requiresReply}}.
     *
     * @return the requires reply flag.
     */
    String requiresReply() default "true";

    String[] adviceChain() default {};

    /**
     * Specify the maximum amount of time in milliseconds to wait when sending a reply
     * {@link org.springframework.messaging.Message} to the {@code outputChannel}.
     * Defaults to {@code -1} - blocking indefinitely.
     * It is applied only if the output channel has some 'sending' limitations, e.g.
     * {@link org.springframework.integration.channel.QueueChannel} with
     * fixed a 'capacity'. In this case a {@link org.springframework.messaging.MessageDeliveryException} is thrown.
     * The 'sendTimeout' is ignored in case of
     * {@link org.springframework.integration.channel.AbstractSubscribableChannel} implementations.
     * Can be specified as 'property placeholder', e.g. {@code ${spring.integration.sendTimeout}}.
     *
     * @return The timeout for sending results to the reply target (in milliseconds)
     */
    String sendTimeout() default "";

    /**
     * The {@link org.springframework.context.SmartLifecycle} {@code autoStartup} option.
     * Can be specified as 'property placeholder', e.g. {@code ${foo.autoStartup}}.
     * Defaults to {@code true}.
     *
     * @return the auto startup {@code boolean} flag.
     */
    String autoStartup() default "";

    /**
     * Specify a {@link org.springframework.context.SmartLifecycle} {@code phase} option.
     * Defaults {@code 0} for {@link org.springframework.integration.endpoint.PollingConsumer}
     * and {@code Integer.MIN_VALUE} for {@link org.springframework.integration.endpoint.EventDrivenConsumer}.
     * Can be specified as 'property placeholder', e.g. {@code ${foo.phase}}.
     *
     * @return the {@code SmartLifecycle} phase.
     */
    String phase() default "";

    /**
     * @return the {@link Poller} options for a polled endpoint
     * ({@link org.springframework.integration.scheduling.PollerMetadata}).
     * This attribute is an {@code array} just to allow an empty default (no poller).
     * Only one {@link Poller} element is allowed.
     */
    Poller[] poller() default {};
}
