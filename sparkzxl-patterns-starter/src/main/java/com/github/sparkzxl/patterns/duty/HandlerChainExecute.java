package com.github.sparkzxl.patterns.duty;

import cn.hutool.core.bean.OptionalBean;
import com.github.sparkzxl.patterns.annonation.DutyStrategy;
import com.google.common.collect.Lists;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * description: 责任链路管理器
 *
 * @author zhoux
 * @since 2021-07-20 22:06:01
 */
public class HandlerChainExecute {

    private Map<String, List<HandlerInterceptor>> handlerInterceptorMap;


    public void setHandlerInterceptorMap(List<HandlerInterceptor> handlerInterceptorList) {
        handlerInterceptorMap = handlerInterceptorList.stream().collect(Collectors.groupingBy(handlerInterceptor ->
                Objects.requireNonNull(AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), DutyStrategy.class)).type()));
    }

    public HandlerChainExecute addInterceptor(HandlerInterceptor handlerInterceptor) {
        String type = Objects.requireNonNull(AnnotationUtils.findAnnotation(handlerInterceptor.getClass(), DutyStrategy.class)).type();
        List<HandlerInterceptor> handlerInterceptors = OptionalBean.ofNullable(handlerInterceptorMap.get(type)).orElseGet(Lists::newArrayList);
        handlerInterceptorMap.put(type, handlerInterceptors);
        return this;
    }

    public <R, T> R execute(String type, T t) {
        List<HandlerInterceptor> handlerInterceptorList = handlerInterceptorMap.get(type);
        R r = null;
        if (!handlerInterceptorList.isEmpty()) {
            for (HandlerInterceptor<R, T> interceptor : handlerInterceptorList) {
                r = interceptor.handle(t);
            }
        }
        return r;
    }

}
