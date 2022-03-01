package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * description:  Header Parameter Data
 *
 * @author zhouxinlei
 * @date 2022-01-10 10:26:32
 */
@Join
public class HeaderParameterData implements ParameterData {

    @Override
    public String builder(String paramName, ServerWebExchange exchange) {
        List<String> headers = exchange.getRequest().getHeaders().get(paramName);
        if (CollectionUtils.isEmpty(headers)) {
            return "";
        }
        return headers.get(0);
    }
}
