package com.sparksys.patterns.strategy;

import com.sparksys.patterns.annonation.HandlerType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * description:业务处理策略类 选择器
 *
 * @author: zhouxinlei
 * @date: 2020-08-03 15:07:54
 */
@Service
public class BusinessHandlerChooser {

    private Map<HandlerType, BusinessHandler> businessHandlerMap;

    @Autowired
    public void setBusinessHandlerMap(List<BusinessHandler> orderHandlers) {
        // 注入各种类型的订单处理类
        businessHandlerMap = orderHandlers.stream().collect(
                Collectors.toMap(orderHandler -> AnnotationUtils.findAnnotation(orderHandler.getClass(), HandlerType.class),
                        v -> v, (v1, v2) -> v1));
    }

    public BusinessHandler businessHandlerChooser(String type, String source) {
        HandlerType orderHandlerType = new HandlerTypeImpl(type, source);
        return businessHandlerMap.get(orderHandlerType);
    }
}
