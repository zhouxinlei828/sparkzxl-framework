package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * The type domain parameter data.
 *
 * @author zhouxinlei
 */
@Join
public class DomainParameterData implements ParameterData {

    @Override
    public String builder(final String paramName, final ServerWebExchange exchange) {
        return exchange.getRequest().getURI().getHost();
    }
}
