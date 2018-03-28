package com.vine.commons.cloudstream.annotation;

import org.springframework.integration.annotation.MessageEndpoint;

import java.lang.annotation.*;

/**
 * @author vrustia - 12/7/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@MessageEndpoint
public @interface OwensMessageEndpoint {

    /**
     * The value may indicate a suggestion for a logical component name,
     * to be turned into a Spring bean in case of an autodetected component.
     *
     * @return the suggested component name, if any
     */
    String value() default "";
}
