package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.context.GatewayContext;
import io.netty.buffer.ByteBuf;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.PubicReactorServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

/**
 * description: 缓存body响应
 *
 * @author zhouxinlei
 */
public class CachedBodyResponse extends PubicReactorServerHttpResponse {

    private ServerWebExchange exchange;

    public CachedBodyResponse(HttpServerResponse response, DataBufferFactory bufferFactory, ServerWebExchange exchange) {
        super(response, bufferFactory);
        this.exchange = exchange;
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        HttpServerResponse nativeResponse = super.getNativeResponse();
        return nativeResponse.send(toByteBufs(publisher)).then();
    }

    private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return DataBufferUtils.join(dataBuffers)
                .doOnNext(this::cacheResponseBody)
                .map(NettyDataBufferFactory::toByteBuf);
    }

    private void cacheResponseBody(DataBuffer dataBuffer) {
        byte[] content = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(content);
        String responseStr = new String(content);
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        gatewayContext.setResponseBody(responseStr);
        dataBuffer.readPosition(0);
    }

}