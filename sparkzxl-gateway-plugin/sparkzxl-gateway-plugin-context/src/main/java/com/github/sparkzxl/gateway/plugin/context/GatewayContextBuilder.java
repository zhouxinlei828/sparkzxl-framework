package com.github.sparkzxl.gateway.plugin.context;

import org.springframework.web.server.ServerWebExchange;

/**
 * description: gateway context builder
 *
 * @author zhouxinlei
 * @date 2022-01-07 11:07:10
 */
public interface GatewayContextBuilder {

    /**
     * Build gateway context.
     *
     * @param exchange the exchange
     * @return GatewayContext
     */
    GatewayContext build(ServerWebExchange exchange);

}
