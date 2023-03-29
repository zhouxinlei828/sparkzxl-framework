package com.github.sparkzxl.gateway.plugin.dubbo.message;

import com.github.sparkzxl.core.json.JsonUtils;
import java.util.Objects;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * description: Jackson 消息写入
 *
 * @author zhouxinlei
 * @since 2022-08-15 08:45:43
 */
public class JacksonDubboMessageWriter implements DubboMessageWriter {

    @Override
    public Mono<Void> write(ServerWebExchange exchange, Object result) {
        ServerHttpResponse response = exchange.getResponse();
        if (result != null) {
            byte[] bytes = Objects.requireNonNull(JsonUtils.getJson().toJson(result)).getBytes();
            DataBuffer dataBuffer = response.bufferFactory().wrap(bytes);
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Flux.just(dataBuffer));
        }
        return Mono.empty();
    }
}
