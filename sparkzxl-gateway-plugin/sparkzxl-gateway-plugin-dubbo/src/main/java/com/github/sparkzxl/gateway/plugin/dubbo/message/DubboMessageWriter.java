package com.github.sparkzxl.gateway.plugin.dubbo.message;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Dubbo 消息写组件
 *
 * @author LIQIU
 * @date 2021/7/1 12:03
 */
public interface DubboMessageWriter {

    /**
     * 将结果写入response，返回调用端
     *
     * @param exchange Exchange
     * @param result   执行结果
     * @return 写入结果
     */
    Mono<Void> write(ServerWebExchange exchange, Object result);

}
