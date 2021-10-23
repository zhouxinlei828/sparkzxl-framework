package com.github.sparkzxl.gateway.context;

import org.springframework.web.server.ServerWebExchange;

/**
 * description: 上下文数据封装
 *
 * @author zhoux
 * @date 2021-10-23 16:50:56
 */
public interface ContextExtraDataGenerator<T> {

    /**
     * generate context extra data
     *
     * @param serverWebExchange
     * @return
     */
    GatewayContextExtraData<T> generateContextExtraData(ServerWebExchange serverWebExchange);

}
