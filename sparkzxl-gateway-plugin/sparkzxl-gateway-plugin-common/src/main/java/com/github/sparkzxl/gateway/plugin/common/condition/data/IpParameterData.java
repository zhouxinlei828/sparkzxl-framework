package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.gateway.plugin.common.utils.HostAddressUtils;
import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: ip parameter data
 *
 * @author zhouxinlei
 * @date 2022-01-10 11:17:46
 */
@Join
public class IpParameterData implements ParameterData {

    @Override
    public String builder(final String paramName, final ServerWebExchange exchange) {
        return HostAddressUtils.acquireIp(exchange);
    }
}