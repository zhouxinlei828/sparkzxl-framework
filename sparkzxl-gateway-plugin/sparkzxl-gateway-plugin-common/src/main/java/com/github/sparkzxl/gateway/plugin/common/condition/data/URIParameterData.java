package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 获取URI
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:10:49
 */
@Join
public class URIParameterData implements ParameterData {
    @Override
    public String builder(String paramName, ServerWebExchange exchange) {
        return exchange.getRequest().getURI().getPath();
    }
}
