package com.github.sparkzxl.gateway.plugin.dubbo.message;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: Dubbo 消息写组件
 *
 * @author zhouxinlei
 * @since 2022-08-15 13:50:45
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
