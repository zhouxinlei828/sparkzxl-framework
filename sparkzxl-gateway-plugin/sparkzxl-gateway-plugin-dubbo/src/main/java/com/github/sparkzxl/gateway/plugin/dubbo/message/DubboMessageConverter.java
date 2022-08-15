package com.github.sparkzxl.gateway.plugin.dubbo.message;

import org.springframework.web.server.ServerWebExchange;

/**
 * Dubbo 结果转换器
 *
 * @author LIQIU
 * @date 2021/7/1 10:36
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
