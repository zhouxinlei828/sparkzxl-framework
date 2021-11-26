package com.github.sparkzxl.gateway.filter.context;

import com.github.sparkzxl.gateway.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: remove gatewayContext Attribute
 *
 * @author zhoux
 * @date 2021-10-23 17:26:50
 */
@Slf4j
public class RemoveGatewayContextFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).doFinally(s -> exchange.getAttributes().remove(GatewayContext.CACHE_GATEWAY_CONTEXT));
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}