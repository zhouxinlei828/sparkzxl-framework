package com.github.sparkzxl.client.ratelimiter;

import com.github.sparkzxl.client.support.NoFallbackAvailableException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * description: Spring Cloud reactive rate limiter API.
 *
 * @author zhouxinlei
 * @since 2024-07-01 15:22:08
 */
public interface ReactiveRateLimiter {

    default <T> Mono<T> run(Mono<T> toRun) {
        return run(toRun, throwable -> {
            throw new NoFallbackAvailableException(throwable.getMessage(), throwable);
        });
    }

    <T> Mono<T> run(Mono<T> toRun, Function<Throwable, Mono<T>> fallback);

    default <T> Flux<T> run(Flux<T> toRun) {
        return run(toRun, throwable -> {
            throw new NoFallbackAvailableException("No fallback available.", throwable);
        });
    }

    <T> Flux<T> run(Flux<T> toRun, Function<Throwable, Flux<T>> fallback);

}
