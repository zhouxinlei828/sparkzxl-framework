package com.github.sparkzxl.patterns.factory;

import cn.hutool.core.bean.OptionalBean;
import com.github.sparkzxl.patterns.annonation.HandlerChain;
import com.github.sparkzxl.patterns.duty.HandlerInterceptor;
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
 * @date 2021-10-23 13:02:34
 */
public class DefaultHandlerChainFactory implements HandlerChainFactory {

    private final Map<String, List<HandlerInterceptor>> interceptorContainer;

    public DefaultHandlerChainFactory() {
        interceptorContainer = Maps.newHashMap();
    }

    @Override
    public List<HandlerInterceptor> getInterceptorList(String type) {
        return interceptorContainer.get(type);
    }

    public DefaultHandlerChainFactory addInterceptor(HandlerInterceptor handlerInterceptor) {
        String type = Objects.requireNonNull(AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), HandlerChain.class)).type();
        List<HandlerInterceptor> handlerInterceptors = OptionalBean.ofNullable(interceptorContainer.get(type)).orElseGet(Lists::newArrayList);
        interceptorContainer.put(type, handlerInterceptors);
        return this;
    }

}
