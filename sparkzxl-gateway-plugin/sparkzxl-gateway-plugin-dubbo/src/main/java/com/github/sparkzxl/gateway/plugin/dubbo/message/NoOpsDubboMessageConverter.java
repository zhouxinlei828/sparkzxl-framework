package com.github.sparkzxl.gateway.plugin.dubbo.message;

import org.springframework.web.server.ServerWebExchange;

/**
 * 无操作消息转换器
 *
 * @author LIQIU
 * @date 2021/8/13 15:43
 */
public class NoOpsDubboMessageConverter implements DubboMessageConverter {

    @Override
    public Object convert(ServerWebExchange exchange, Object source) {
        return source;
    }
}
