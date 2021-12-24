package com.github.sparkzxl.gateway.filter.log;

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

    public LogBaseSubscriber(Subscriber actual, ServerWebExchange exchange) {
        this.actual = actual;
        this.exchange = exchange;
    }

    @Override
    protected void hookOnNext(Object value) {
        actual.onNext(value);
    }

    @Override
    protected void hookOnComplete() {
        try {
            OptLogUtil.recordLog(exchange);
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
            OptLogUtil.recordLog(exchange);
        } finally {
            actual.onError(t);
        }
    }
}
