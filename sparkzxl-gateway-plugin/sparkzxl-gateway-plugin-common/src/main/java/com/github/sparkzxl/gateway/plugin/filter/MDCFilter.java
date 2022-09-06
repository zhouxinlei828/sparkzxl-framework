package com.github.sparkzxl.gateway.plugin.filter;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.gateway.plugin.common.utils.HostAddressUtils;
import com.github.sparkzxl.gateway.plugin.common.utils.ReactorHttpHelper;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: MDC上下文注入
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:31:43
 */
public class MDCFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        MDC.put(BaseContextConstants.TENANT_ID, ReactorHttpHelper.getHeader(request, BaseContextConstants.TENANT_ID));
        String ipAddress = HostAddressUtils.acquireIp(exchange);
        MDC.put(BaseContextConstants.CLIENT_IP, ipAddress);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
