package com.github.sparkzxl.gateway.plugin.logging;

import com.github.sparkzxl.gateway.common.condition.data.ParameterDataFactory;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.constant.ParameterDataConstant;
import com.github.sparkzxl.gateway.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.plugin.core.context.GatewayContext;
import com.github.sparkzxl.gateway.plugin.core.filter.AbstractGlobalFilter;
import com.github.sparkzxl.gateway.plugin.logging.decorator.LoggingResponseBodyDecorator;
import com.github.sparkzxl.gateway.plugin.logging.service.IOptLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.event.EnableBodyCachingEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.ApplicationContext;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * description: Response log 过滤器
 *
 * @author zhouxinlei
 * @since 2022-01-10 15:41:53
 */
@RequiredArgsConstructor
public class ResponseLogFilter extends AbstractGlobalFilter {

    private final ApplicationContext applicationContext;

    private final IOptLogService optLogService;

    private final Set<String> cacheRequestBodyRouter = new CopyOnWriteArraySet<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        if (!(serverHttpResponse instanceof AbstractServerHttpResponse)) {
            return chain.filter(exchange);
        }
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
        boolean enableLogging =
                Boolean.parseBoolean(ParameterDataFactory.builderData(ParameterDataConstant.ATTRIBUTE, GatewayConstant.ENABLE_LOGGING, exchange));
        if (enableLogging) {
            sendCacheRequestBodyEvent(gatewayContext.getRouteId());
            return chain.filter(exchange.mutate().response(new LoggingResponseBodyDecorator(exchange.getResponse(), exchange, optLogService)).build());
        }
        return chain.filter(exchange);
    }

    private void sendCacheRequestBodyEvent(String routeId) {
        if (!cacheRequestBodyRouter.contains(routeId)) {
            EnableBodyCachingEvent event = new EnableBodyCachingEvent(this, routeId);
            applicationContext.publishEvent(event);
            cacheRequestBodyRouter.add(routeId);
        }
    }

    @Override
    public String named() {
        return FilterEnum.LOGGING.getName();
    }

    @Override
    public int getOrder() {
        return FilterEnum.LOGGING.getCode();
    }
}