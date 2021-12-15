package com.github.sparkzxl.gateway.predicate.processor;

import com.github.sparkzxl.gateway.predicate.support.DynamicRouteConfig;
import com.github.sparkzxl.gateway.predicate.support.PreprocessResult;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

/**
 * description: 动态路由默认处理
 *
 * @author zhoux
 */
public class DefaultDynamicRouteProcessor implements DynamicRouteProcessor {

    @Override
    public Optional<PreprocessResult> preprocess(ServerWebExchange exchange) {
        return Optional.empty();
    }

    @Override
    public Optional<DynamicRouteConfig> processConfig(PreprocessResult preprocessResult, Route route) {
        return Optional.empty();
    }

    @Override
    public Optional<Class<? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass() {
        return Optional.empty();
    }
}
