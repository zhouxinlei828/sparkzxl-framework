package com.github.sparkzxl.patterns.strategy;

import com.github.sparkzxl.patterns.annonation.HandlerType;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description:业务处理策略类 选择器
 *
 * @author zhouxinlei
 */
public class BusinessHandlerChooser {

    private Map<HandlerType, BusinessHandler> businessHandlerMap;

    public void setBusinessHandlerMap(List<BusinessHandler> orderHandlers) {
        // 注入各种类型的订单处理类
        businessHandlerMap = orderHandlers.stream().collect(
                Collectors.toMap(orderHandler -> AnnotationUtils.findAnnotation(orderHandler.getClass(), HandlerType.class),
                        v -> v, (v1, v2) -> v1));
    }

    public <R, T> BusinessHandler<R, T> businessHandlerChooser(String type, String source) {
        HandlerType orderHandlerType = new HandlerTypeImpl(type, source);
        return businessHandlerMap.get(orderHandlerType);
    }
}
