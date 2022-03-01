package com.github.sparkzxl.gateway.plugin.logging.service;

import com.github.sparkzxl.gateway.plugin.properties.LoggingProperties;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 操作日志 服务类
 *
 * @author zhouxinlei
 * @date 2022-01-10 17:38:44
 */
public interface IOptLogService {

    /**
     * 记录日志
     *
     * @param exchange exchange
     */
    void recordLog(ServerWebExchange exchange);
}
