package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: request method parameter data
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:17:00
 */
@Join
public class RequestMethodParameterData implements ParameterData {

    @Override
    public String builder(final String paramName, final ServerWebExchange exchange) {
        return exchange.getRequest().getMethodValue();
    }
}
