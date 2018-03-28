package com.vine.commons.cloudstream.util;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AnnotatedBeanLocator {

    private final ConfigurableApplicationContext applicationContext;

    public AnnotatedBeanLocator(ApplicationContext applicationContext) {

        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
    }

    public <T extends Annotation> List<T> getBeansMethodAnnotation(Class<? extends Annotation> typeAnnotation, Class<T> methodAnnotation) {

        List<T> result = new ArrayList<T>();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(typeAnnotation);

        for (Object bean : beansWithAnnotation.values()) {

            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Method[] methods = null;

            if (AopUtils.isJdkDynamicProxy(bean)) {
                Class<?>[] interfaces = targetClass.getInterfaces();
                for (Class<?> interfaceClazz : interfaces) {
                    if (interfaceClazz.isAnnotationPresent(typeAnnotation)) {
                        methods = interfaceClazz.getDeclaredMethods();
                        break;
                    }
                }
            } else {
                methods = targetClass.getDeclaredMethods();
            }

            for (Method m : methods) {
                if (m.isAnnotationPresent(methodAnnotation)) {
                    result.add(m.getAnnotation(methodAnnotation));
                }
            }
        }

        return result;
    }

    public <T extends Annotation> List<AnnotatedMethod<T>> getBeansAnnotatedMethod(Class<? extends Annotation> typeAnnotation, Class<T> methodAnnotation) {

        List<AnnotatedMethod<T>> result = new ArrayList<>();
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(typeAnnotation);

        for (Object bean : beansWithAnnotation.values()) {

            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Method[] methods = null;

            if (AopUtils.isJdkDynamicProxy(bean)) {
                Class<?>[] interfaces = targetClass.getInterfaces();
                for (Class<?> interfaceClazz : interfaces) {
                    if (interfaceClazz.isAnnotationPresent(typeAnnotation)) {
                        methods = interfaceClazz.getDeclaredMethods();
                        break;
                    }
                }
            } else {
                methods = targetClass.getDeclaredMethods();
            }

            for (Method m : methods) {
                if (m.isAnnotationPresent(methodAnnotation)) {
                    result.add(new AnnotatedMethod<T>().setAnnotation(m.getAnnotation(methodAnnotation)).setMethod(m));
                }
            }
        }

        return result;
    }
}