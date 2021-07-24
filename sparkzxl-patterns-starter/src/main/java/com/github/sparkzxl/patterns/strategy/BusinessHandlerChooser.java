package com.github.sparkzxl.patterns.strategy;

import com.github.sparkzxl.patterns.annonation.BusinessStrategy;
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

    private Map<BusinessStrategy, BusinessHandler> businessHandlerMap;

    public void setBusinessHandlerMap(List<BusinessHandler> businessHandlerList) {
        // 注入各种类型的订单处理类
        businessHandlerMap = businessHandlerList.stream().collect(
                Collectors.toMap(businessHandler -> AnnotationUtils.findAnnotation(businessHandler.getClass(), BusinessStrategy.class),
                        v -> v, (v1, v2) -> v1));
    }

    public <R, T> BusinessHandler<R, T> businessHandlerChooser(String type, String source) {
        BusinessStrategy businessStrategy = new BusinessStrategyImpl(type, source);
        return businessHandlerMap.get(businessStrategy);
    }
}
