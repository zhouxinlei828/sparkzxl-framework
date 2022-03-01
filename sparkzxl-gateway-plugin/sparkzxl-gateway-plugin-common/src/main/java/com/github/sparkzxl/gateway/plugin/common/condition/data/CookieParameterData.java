package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.Join;
import org.springframework.http.HttpCookie;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * description: cookie parameter data
 *
 * @author zhouxinlei
 * @date 2022-01-10 11:18:32
 */
@Join
public class CookieParameterData implements ParameterData {

    @Override
    public String builder(final String paramName, final ServerWebExchange exchange) {
        List<HttpCookie> cookies = exchange.getRequest().getCookies().get(paramName);
        if (CollectionUtils.isEmpty(cookies)) {
            return "";
        }
        return cookies.get(0).getValue();
    }
}