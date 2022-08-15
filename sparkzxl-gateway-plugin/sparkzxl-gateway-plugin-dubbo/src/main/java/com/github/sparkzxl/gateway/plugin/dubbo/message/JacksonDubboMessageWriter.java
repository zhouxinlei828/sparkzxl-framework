package com.github.sparkzxl.gateway.plugin.dubbo.message;

import lombok.Setter;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Encoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-15 08:45:43
 */
public class JacksonDubboMessageWriter implements DubboMessageWriter {

    @Setter
    private Encoder<Object> encoder = new Jackson2JsonEncoder();

    @Override
    public Mono<Void> write(ServerWebExchange exchange, Object result) {
        ServerHttpResponse response = exchange.getResponse();
        if (result != null) {
            DataBuffer dataBuffer = encoder.encodeValue(result, response.bufferFactory(), ResolvableType.forClass(String.class), MimeTypeUtils.APPLICATION_JSON, null);
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Flux.just(dataBuffer));
        }
        return Mono.empty();
    }
}