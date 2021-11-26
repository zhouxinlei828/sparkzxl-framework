package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * description: 请求日志过滤器
 *
 * @author zhouxinlei
 * @date 2021-11-26 13:55
 */
public class AccessLogFilter implements GlobalFilter, Ordered {

    private static final String HTTP_SCHEME = "http";

    private static final String HTTPS_SCHEME = "https";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        if (gatewayContext.isReadRequestData()) {
            ServerHttpRequest request = exchange.getRequest();
            URI requestURI = request.getURI();
            String scheme = requestURI.getScheme();
            if ((!HTTP_SCHEME.equalsIgnoreCase(scheme) && !HTTPS_SCHEME.equals(scheme))) {
                return chain.filter(exchange);
            }
            return chain.filter(exchange).transform(p ->
                    actual ->
                            p.subscribe(new LogBaseSubscriber(actual, exchange)));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.REQUEST_LOG_FILTER.getOrder();
    }
}
