package com.vine.commons.cloudstream.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by vrustia on 3/8/18.
 */
public class AnnotatedMethod<T extends Annotation> {
    private T annotation;
    private Method method;

    public T getAnnotation() {
        return annotation;
    }

    public AnnotatedMethod<T> setAnnotation(T annotation) {
        this.annotation = annotation;
        return this;
    }

    public Method getMethod() {
        return method;
    }

    public AnnotatedMethod<T> setMethod(Method method) {
        this.method = method;
        return this;
    }
}
