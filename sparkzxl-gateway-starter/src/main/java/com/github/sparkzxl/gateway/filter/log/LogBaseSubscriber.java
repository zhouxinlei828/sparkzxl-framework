package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.BaseSubscriber;

/**
 * description: 日志订阅
 *
 * @author zhouxinlei
 * @date 2021-12-24 08:57:25
 */
@Slf4j
public class LogBaseSubscriber extends BaseSubscriber {

    private final Subscriber actual;

    private final ServerWebExchange exchange;

    private final LogRequestProperties logging;

    public LogBaseSubscriber(Subscriber actual, ServerWebExchange exchange, LogRequestProperties logging) {
        this.actual = actual;
        this.exchange = exchange;
        this.logging = logging;
    }

    @Override
    protected void hookOnNext(Object value) {
        actual.onNext(value);
    }

    @Override
    protected void hookOnComplete() {
        try {
            OptLogUtil.recordLog(exchange,logging);
        } finally {
            actual.onComplete();
        }
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        actual.onSubscribe(this);
    }

    @Override
    protected void hookOnError(Throwable t) {
        try {
            OptLogUtil.recordLog(exchange,logging);
        } finally {
            actual.onError(t);
        }
    }
}
