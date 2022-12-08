package com.github.sparkzxl.gateway.plugin.exception;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.gateway.plugin.exception.factory.ExceptionHandlerStrategyFactory;
import com.github.sparkzxl.gateway.plugin.exception.result.ExceptionHandlerResult;
import com.github.sparkzxl.gateway.plugin.exception.strategy.DefaultExceptionHandlerStrategy;
import com.github.sparkzxl.gateway.plugin.exception.strategy.ExceptionHandlerStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.reactive.result.view.ViewResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * description: Json Exception Handler {@link org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler}
 *
 * @author zhoux
 */
@Slf4j
public class JsonExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * temporary cache exception handler result for another method
     */
    private final ThreadLocal<ExceptionHandlerResult> exceptionHandlerResult = new ThreadLocal<>();
    /**
     * Strategy Factory
     */
    private final ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory;
    /**
     * MessageReader
     */
    private List<HttpMessageReader<?>> messageReaders = Collections.emptyList();
    /**
     * MessageWriter
     */
    private List<HttpMessageWriter<?>> messageWriters = Collections.emptyList();
    /**
     * ViewResolvers
     */
    private List<ViewResolver> viewResolvers = Collections.emptyList();

    public JsonExceptionHandler(ExceptionHandlerStrategyFactory exceptionHandlerStrategyFactory) {
        Assert.notNull(exceptionHandlerStrategyFactory, "'ExceptionHandlerStrategyFactory' must not be null");
        this.exceptionHandlerStrategyFactory = exceptionHandlerStrategyFactory;
    }

    public void setMessageReaders(List<HttpMessageReader<?>> messageReaders) {
        Assert.notNull(messageReaders, "'messageReaders' must not be null");
        this.messageReaders = messageReaders;
    }

    public void setViewResolvers(List<ViewResolver> viewResolvers) {
        this.viewResolvers = viewResolvers;
    }

    public void setMessageWriters(List<HttpMessageWriter<?>> messageWriters) {
        Assert.notNull(messageWriters, "'messageWriters' must not be null");
        this.messageWriters = messageWriters;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        /*
         * get strategy
         */
        ExceptionHandlerStrategy strategy = exceptionHandlerStrategyFactory.getStrategy(ex.getClass());
        ExceptionHandlerResult result = strategy.handleException(ex);

        // log error
        String message = buildMessage(request, ex);
        log.error(message);
        /*
         * if is Default Strategy Print Stack Trace
         */
        if (strategy instanceof DefaultExceptionHandlerStrategy && log.isDebugEnabled()) {
            log.debug("Global Exception Handler Request Path:{},Exception Detail:{}", request.getPath(), ExceptionUtils.getStackTrace(ex));
        }
        exceptionHandlerResult.set(result);
        ServerRequest newRequest = ServerRequest.create(exchange, this.messageReaders);
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse).route(newRequest)
                .switchIfEmpty(Mono.error(ex))
                .flatMap((handler) -> handler.handle(newRequest))
                .flatMap((response) -> write(exchange, response));

    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        ExceptionHandlerResult result = exceptionHandlerResult.get();
        Mono<ServerResponse> responseMono = ServerResponse.status(result.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(result.getResponseResult()));
        exceptionHandlerResult.remove();
        return responseMono;
    }

    private Mono<? extends Void> write(ServerWebExchange exchange,
                                       ServerResponse response) {
        exchange.getResponse().getHeaders()
                .setContentType(response.headers().getContentType());
        return response.writeTo(exchange, new ResponseContext());
    }

    /**
     * 构建异常信息
     *
     * @param request 请求
     * @param ex      异常
     * @return String
     */
    private String buildMessage(ServerHttpRequest request, Throwable ex) {
        return StrFormatter.format("Failed to handle request [{}] {} :[{}]", Objects.requireNonNull(request.getMethod()).name(), request.getPath(),
                ex.getMessage());
    }

    private class ResponseContext implements ServerResponse.Context {

        @Override
        public List<HttpMessageWriter<?>> messageWriters() {
            return JsonExceptionHandler.this.messageWriters;
        }

        @Override
        public List<ViewResolver> viewResolvers() {
            return JsonExceptionHandler.this.viewResolvers;
        }

    }
}
