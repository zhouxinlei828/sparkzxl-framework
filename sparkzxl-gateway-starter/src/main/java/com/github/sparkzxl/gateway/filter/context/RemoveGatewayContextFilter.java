package com.github.sparkzxl.gateway.filter.context;

import com.github.sparkzxl.gateway.constant.ExchangeAttributeConstant;
import com.github.sparkzxl.gateway.context.GatewayContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * description: remove gatewayContext Attribute
 *
 * @author zhoux
 */
@Slf4j
public class RemoveGatewayContextFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).doFinally(s -> {
            Map<String, Object> exchangeAttributes = exchange.getAttributes();
            exchangeAttributes.remove(GatewayContext.CACHE_GATEWAY_CONTEXT);
            exchangeAttributes.remove(ExchangeAttributeConstant.SYSTEM_REQUEST_PARAM);
            exchangeAttributes.remove(ExchangeAttributeConstant.USER_INFO);
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}