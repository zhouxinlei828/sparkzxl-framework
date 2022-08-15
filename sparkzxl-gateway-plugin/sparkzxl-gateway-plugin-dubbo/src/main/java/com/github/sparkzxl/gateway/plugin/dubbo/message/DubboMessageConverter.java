package com.github.sparkzxl.gateway.plugin.dubbo.message;

import org.springframework.web.server.ServerWebExchange;

/**
 * description: Dubbo 结果转换器
 *
 * @author zhouxinlei
 * @since 2022-08-15 13:50:36
 */
public interface DubboMessageConverter {

    /**
     * 转换Dubbo结果
     *
     * @param exchange Exchange
     * @param source   原数据
     * @return 转换后数据
     */
    Object convert(ServerWebExchange exchange, Object source);

}
