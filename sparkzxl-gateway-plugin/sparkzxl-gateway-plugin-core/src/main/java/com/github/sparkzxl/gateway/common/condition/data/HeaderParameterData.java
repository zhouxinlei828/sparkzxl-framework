package com.github.sparkzxl.gateway.common.condition.data;

import com.github.sparkzxl.spi.Join;
import java.util.List;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * description:  Header Parameter Data
 *
 * @author zhouxinlei
 * @since 2022-01-10 10:26:32
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
