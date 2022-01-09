package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.event.EnableBodyCachingEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description: 缓存响应body过滤器
 *
 * @author zhouxinlei
 */
@Component
@RequiredArgsConstructor
public class ResponseLogCachedBodyFilter implements GlobalFilter, Ordered {

    private final ApplicationContext applicationContext;

    private final Set<String> cacheRequestBodyRouter = new CopyOnWriteArraySet<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        if (!(serverHttpResponse instanceof AbstractServerHttpResponse)) {
            return chain.filter(exchange);
        }
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.GATEWAY_CONTEXT_CONSTANT);
        if (gatewayContext.isOutputLog()) {
            sendCacheRequestBodyEvent(gatewayContext);
            return chain.filter(exchange.mutate()
                    .response(new HttpResponseBodyDecorator(exchange.getResponse(),
                            exchange))
                    .build());
        }
        return chain.filter(exchange);
    }

    private void sendCacheRequestBodyEvent(GatewayContext gatewayContext) {
        String routeId = gatewayContext.getRouteId();
        if (!cacheRequestBodyRouter.contains(routeId)) {
            EnableBodyCachingEvent event = new EnableBodyCachingEvent(this, routeId);
            applicationContext.publishEvent(event);
            cacheRequestBodyRouter.add(routeId);
        }
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.RESPONSE_DATA_FILTER.getOrder();
    }
}
