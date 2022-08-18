package com.github.sparkzxl.gateway.plugin;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.gateway.plugin.filter.AbstractGlobalFilter;
import com.github.sparkzxl.monitor.MonitorContext;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: 生成日志链路追踪id，并传入header中
 *
 * @author zhouxinlei
 * @since 2022-08-18 14:51:21
 */
@Component
public class TraceFilter extends AbstractGlobalFilter {

    @Autowired
    private MonitorContext monitorContext;

    @Override
    public String named() {
        return "trace";
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //链路追踪id
        String traceId = monitorContext.getTraceId();
        MDC.put(BaseContextConstants.LOG_TRACE_ID, traceId);
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate()
                .headers(h -> h.add(BaseContextConstants.TRACE_ID_HEADER, traceId)).build();
        ServerWebExchange build = exchange.mutate().request(serverHttpRequest).build();
        return chain.filter(build);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
