package com.github.sparkzxl.gateway.plugin.dubbo.route;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 于判断请求的路由是否是Dubbo路由
 *
 * @author zhouxinlei
 * @since 2022-08-12 16:14:15
 */
public interface DubboRoutePredicate {

    /**
     * 判断是否是Dubbo路由
     *
     * @param exchange exchange
     * @param route    gateway路由
     * @return boolean
     */
    boolean test(ServerWebExchange exchange, Route route);
}
