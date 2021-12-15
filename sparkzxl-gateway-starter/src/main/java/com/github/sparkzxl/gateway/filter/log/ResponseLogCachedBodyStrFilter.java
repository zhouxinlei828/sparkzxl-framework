package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.event.EnableBodyCachingEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description: 缓存响应body过滤器
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class ResponseLogCachedBodyStrFilter implements GlobalFilter, Ordered {

    private final ApplicationContext applicationContext;

    private final Set<String> cacheRequestBodyRouter = new CopyOnWriteArraySet<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        if (!(serverHttpResponse instanceof AbstractServerHttpResponse)) {
            return chain.filter(exchange);
        }

        AbstractServerHttpResponse response = (AbstractServerHttpResponse) serverHttpResponse;
        if (needCacheBody(exchange, chain)) {
            sendCacheRequestBodyEvent(exchange);
            return chain.filter(exchange.mutate()
                    .response(new CachedBodyResponse(response.getNativeResponse(),
                            response.bufferFactory(), exchange))
                    .build());
        }
        return chain.filter(exchange);
    }

    /**
     * 是否需要缓存 响应 body
     *
     * @param exchange
     * @param chain
     * @return
     */
    private boolean needCacheBody(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        return gatewayContext.isReadResponseData();
    }

    private void sendCacheRequestBodyEvent(ServerWebExchange exchange) {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String routeId = route.getId();
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
