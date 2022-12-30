package com.github.sparkzxl.gateway.plugin.dubbo.message;

import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 无操作消息转换器
 *
 * @author zhouxinlei
 * @since 2022-08-15 13:51:07
 */
@Join
public class NoOpsDubboMessageConverter implements DubboMessageConverter {

    @Override
    public Object convert(ServerWebExchange exchange, Object source) {
        return source;
    }
}
