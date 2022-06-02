package com.github.sparkzxl.feign.exception;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.github.sparkzxl.feign.properties.FeignProperties;
import com.github.sparkzxl.feign.properties.PredicateDefinition;
import com.google.common.collect.Maps;
import feign.Response;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * description: 异常定义定位器实现类
 *
 * @author zhouxinlei
 * @since 2022-05-10 11:39:50
 */
public class ExceptionDefinitionLocatorImpl implements ExceptionDefinitionLocator {
    private final Map<String, ExceptionPredicateFactory> predicateFactoryMap;
    private final FeignProperties.FeignExceptionProperties feignExceptionProperties;

    public ExceptionDefinitionLocatorImpl(FeignProperties.FeignExceptionProperties feignExceptionProperties,
                                          List<ExceptionPredicateFactory> predicateFactories) {
        this.feignExceptionProperties = feignExceptionProperties;
        predicateFactoryMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(predicateFactories)) {
            predicateFactories.forEach(predicateFactory -> {
                predicateFactoryMap.put(predicateFactory.name(), predicateFactory);
            });
        }
    }

    @Override
    public ExceptionPredicate<Response> getExceptionPredicate() {
        PredicateDefinition predicateDefinition = feignExceptionProperties.getPredicate();
        ExceptionPredicateFactory factory = predicateFactoryMap.get(predicateDefinition.getName());
        if (factory == null) {
            throw new IllegalArgumentException("Unable to find RoutePredicateFactory with name " + predicateDefinition.getName());
        }
        Map<String, String> args = predicateDefinition.getArgs();
        Object config = factory.newConfig();
        if (MapUtil.isNotEmpty(args)) {
            for (String key : args.keySet()) {
                ReflectUtil.setFieldValue(config, key, args.get(key));
            }
        }
        return factory.apply(config);
    }
}
