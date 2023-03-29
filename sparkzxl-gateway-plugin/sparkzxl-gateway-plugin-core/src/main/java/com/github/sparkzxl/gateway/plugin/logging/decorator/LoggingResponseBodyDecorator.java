package com.github.sparkzxl.gateway.plugin.logging.decorator;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.logging.LogContext;
import com.github.sparkzxl.gateway.plugin.logging.service.IOptLogService;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * description: Http响应体装饰器
 *
 * @author zhouxinlei
 * @since 2021-12-24 08:47
 */
public class LoggingResponseBodyDecorator extends ServerHttpResponseDecorator {

    private final ServerWebExchange exchange;

    private final IOptLogService optLogService;

    public LoggingResponseBodyDecorator(ServerHttpResponse delegate, ServerWebExchange exchange, IOptLogService optLogService) {
        super(delegate);
        this.exchange = exchange;
        this.optLogService = optLogService;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        if (Objects.equals(getStatusCode(), HttpStatus.OK) && body instanceof Flux) {
            // 获取ContentType，判断是否返回JSON格式数据
            String originalResponseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
            if (StringUtils.isNotBlank(originalResponseContentType) && originalResponseContentType.contains(
                    MediaType.APPLICATION_JSON_VALUE)) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    //解决返回体分段传输
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer dataBuffer = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(content);
                    DataBufferUtils.release(dataBuffer);
                    String responseData = new String(content, StandardCharsets.UTF_8);
                    // 缓存请求响应结果数据
                    cacheResponseBody(responseData);
                    // 二次处理（加密/过滤等）如果不需要做二次处理可直接跳过下行
                    // body转码
                    byte[] uppedContent = new String(responseData.getBytes(), StandardCharsets.UTF_8).getBytes();
                    originalResponse.getHeaders().setContentLength(uppedContent.length);
                    return bufferFactory.wrap(uppedContent);
                }));
            }
        }
        return super.writeWith(body);
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body).flatMapSequential(p -> p));
    }

    private void cacheResponseBody(String responseData) {
        LogContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_LOG_CONTEXT_CONSTANT);
        gatewayContext.setResponseBody(responseData);
        optLogService.recordLog(exchange);
    }

    @Override
    public boolean setRawStatusCode(Integer value) {
        return super.setRawStatusCode(value);
    }

    @Override
    public Integer getRawStatusCode() {
        return super.getRawStatusCode();
    }
}
