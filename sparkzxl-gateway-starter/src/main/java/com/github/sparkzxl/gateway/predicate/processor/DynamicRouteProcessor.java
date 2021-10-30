package com.github.sparkzxl.gateway.predicate.processor;

import com.github.sparkzxl.gateway.predicate.support.DynamicRouteConfig;
import com.github.sparkzxl.gateway.predicate.support.PreprocessResult;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

/**
 * description: 动态路由处理器
 *
 * @author zhoux
 * @date 2021-10-23 16:48:47
 */
public interface DynamicRouteProcessor<T> {

    /**
     * preprocess action
     *
     * @param exchange ServerWebExchange
     * @return process Result ,if result is Optional.empty(),then dynamic predicate not working
     */
    Optional<PreprocessResult<T>> preprocess(ServerWebExchange exchange);

    /**
     * process to unify config for predicate
     *
     * @param preprocessResult pre process result
     * @param route            current route
     * @return
     */
    Optional<DynamicRouteConfig> processConfig(PreprocessResult<T> preprocessResult, Route route);

    /**
     * target predicate bean 's class
     *
     * @return RoutePredicateFactory Class
     */
    Optional<Class<? extends AbstractRoutePredicateFactory>> targetPredicateBeanClass();
}
