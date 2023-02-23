package com.github.sparkzxl.patterns.factory;

import com.github.sparkzxl.patterns.annonation.HandlerChain;
import com.github.sparkzxl.patterns.pipeline.HandlerInterceptor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * description: 责任链工厂实现类
 *
 * @author zhoux
 */
public class DefaultHandlerInterceptorFactory implements HandlerInterceptorFactory {

    private final Map<String, List<HandlerInterceptor>> interceptorContainer;

    public DefaultHandlerInterceptorFactory() {
        interceptorContainer = Maps.newHashMap();
    }

    @Override
    public List<HandlerInterceptor> getInterceptorList(String type) {
        return interceptorContainer.get(type);
    }

    public DefaultHandlerInterceptorFactory addInterceptor(HandlerInterceptor handlerInterceptor) {
        String type = Objects.requireNonNull(AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), HandlerChain.class)).type();
        List<HandlerInterceptor> handlerInterceptors =
                Optional.ofNullable(interceptorContainer.get(type)).orElseGet(Lists::newArrayList);
        interceptorContainer.put(type, handlerInterceptors);
        return this;
    }

}
