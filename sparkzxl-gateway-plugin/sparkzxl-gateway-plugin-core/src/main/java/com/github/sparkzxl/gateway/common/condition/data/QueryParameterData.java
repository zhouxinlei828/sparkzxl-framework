package com.github.sparkzxl.gateway.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 获取查询参数数据
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:11:55
 */
@Join
public class QueryParameterData implements ParameterData {

    @Override
    public String builder(String paramName, ServerWebExchange exchange) {
        return exchange.getRequest().getQueryParams().getFirst(paramName);
    }
}
