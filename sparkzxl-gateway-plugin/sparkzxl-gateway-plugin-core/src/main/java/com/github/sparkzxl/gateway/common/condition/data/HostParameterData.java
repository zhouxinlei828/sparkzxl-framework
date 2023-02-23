package com.github.sparkzxl.gateway.common.condition.data;

import com.github.sparkzxl.gateway.utils.HostAddressUtils;
import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: The type Host parameter data.
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:16:15
 */
@Join
public class HostParameterData implements ParameterData {

    @Override
    public String builder(final String paramName, final ServerWebExchange exchange) {
        return HostAddressUtils.acquireHost(exchange);
    }
}
