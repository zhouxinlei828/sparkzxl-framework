package com.github.sparkzxl.client.circuitbreaker;

import com.github.sparkzxl.client.support.NoFallbackAvailableException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Spring Cloud reactive circuit breaker API.
 *
 * @author Ryan Baxter
 */
public interface ReactiveCircuitBreaker {

    default <T> Mono<T> run(Mono<T> toRun) {
        return run(toRun, throwable -> {
            throw new NoFallbackAvailableException("No fallback available.", throwable);
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
