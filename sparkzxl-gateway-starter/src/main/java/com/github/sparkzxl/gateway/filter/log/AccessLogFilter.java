package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.constant.enums.FilterOrderEnum;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AccessLogFilter implements GlobalFilter, Ordered {

    private final GatewayPluginProperties gatewayPluginProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.GATEWAY_CONTEXT_CONSTANT);
        LogRequestProperties logging = gatewayPluginProperties.getLogging();
        boolean outputLog = Boolean.FALSE;
        if (logging.isEnabled()) {
            if (logging.isAll()) {
                outputLog = Boolean.TRUE;
            } else {
                outputLog = logging.match(gatewayContext.getRouteId(), gatewayContext.getPath());
            }
        }
        gatewayContext.setOutputLog(outputLog);
        exchange.getAttributes().put(GatewayContext.GATEWAY_CONTEXT_CONSTANT, gatewayContext);
        if (outputLog) {
            return chain.filter(exchange).transform(p ->
                    actual -> p.subscribe(new LogBaseSubscriber(actual, exchange, logging)));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.REQUEST_LOG_FILTER.getOrder();
    }
}
