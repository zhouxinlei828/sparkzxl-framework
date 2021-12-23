package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.CacheGatewayContext;
import com.github.sparkzxl.gateway.entity.RoutePath;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.properties.LogProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: 请求日志过滤器
 *
 * @author zhouxinlei
 */
public class AccessLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        CacheGatewayContext cacheGatewayContext = exchange.getAttribute(CacheGatewayContext.CACHE_GATEWAY_CONTEXT);
        LogProperties logging = cacheGatewayContext.getLogging();
        boolean outputLog = Boolean.FALSE;
        if (logging.isEnabled()) {
            if (logging.isAll()) {
                outputLog = Boolean.TRUE;
            } else {
                RoutePath routePath = cacheGatewayContext.getRoutePath();
                outputLog = logging.match(routePath.getRouteId(), routePath.getPath());
            }
        }
        cacheGatewayContext.setOutputLog(outputLog);
        exchange.getAttributes().put(CacheGatewayContext.CACHE_GATEWAY_CONTEXT, cacheGatewayContext);
        if (outputLog) {
            return chain.filter(exchange).transform(p ->
                    actual -> p.subscribe(new LogBaseSubscriber(actual, exchange)));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.REQUEST_LOG_FILTER.getOrder();
    }
}
