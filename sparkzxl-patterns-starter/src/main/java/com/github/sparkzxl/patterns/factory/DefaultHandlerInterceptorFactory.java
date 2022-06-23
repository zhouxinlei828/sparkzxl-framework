package com.github.sparkzxl.patterns.factory;

import cn.hutool.core.bean.OptionalBean;
import com.github.sparkzxl.patterns.annonation.HandlerChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * description: 责任链工厂实现类
 *
 * @author zhoux
 */
public class DefaultHandlerInterceptorFactory implements HandlerInterceptorFactory {

    private final Map<String, List<com.github.sparkzxl.patterns.pipeline.HandlerInterceptor>> interceptorContainer;

    public DefaultHandlerInterceptorFactory() {
        interceptorContainer = Maps.newHashMap();
    }

    @Override
    public List<com.github.sparkzxl.patterns.pipeline.HandlerInterceptor> getInterceptorList(String type) {
        return interceptorContainer.get(type);
    }

    public DefaultHandlerInterceptorFactory addInterceptor(com.github.sparkzxl.patterns.pipeline.HandlerInterceptor handlerInterceptor) {
        String type = Objects.requireNonNull(AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), HandlerChain.class)).type();
        List<com.github.sparkzxl.patterns.pipeline.HandlerInterceptor> handlerInterceptors =
                OptionalBean.ofNullable(interceptorContainer.get(type)).orElseGet(Lists::newArrayList);
        interceptorContainer.put(type, handlerInterceptors);
        return this;
    }

}
