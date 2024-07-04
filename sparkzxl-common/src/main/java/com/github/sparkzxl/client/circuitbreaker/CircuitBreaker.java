package com.github.sparkzxl.client.circuitbreaker;

import com.github.sparkzxl.client.support.NoFallbackAvailableException;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Spring Cloud circuit breaker.
 *
 * @author Ryan Baxter
 */
public interface CircuitBreaker {

    default <T> T run(Supplier<T> toRun) {
        return run(toRun, throwable -> {
            throw new NoFallbackAvailableException("No fallback available.", throwable);
        });
    };

    <T> T run(Supplier<T> toRun, Function<Throwable, T> fallback);

}
