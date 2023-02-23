package com.github.sparkzxl.gateway.plugin.dubbo.rule;

import com.github.sparkzxl.gateway.plugin.dubbo.message.DubboMessageConverter;
import com.github.sparkzxl.gateway.rule.RuleHandle;
import com.github.sparkzxl.spi.ExtensionLoader;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: dubbo rule handle
 *
 * @author zhouxinlei
 * @since 2022-12-30 16:38:53
 */
public class DubboRuleHandle implements RuleHandle {

    private String converter;

    public String getConverter() {
        return converter;
    }

    public void setConverter(String converter) {
        this.converter = converter;
    }

    public Object convert(ServerWebExchange exchange, Object source) {
        DubboMessageConverter dubboMessageConverter = ExtensionLoader
                .getExtensionLoader(DubboMessageConverter.class)
                .getJoin(converter);
        return dubboMessageConverter.convert(exchange, source);
    }

}
