package com.github.sparkzxl.client.ratelimiter;


import com.github.sparkzxl.client.ConfigBuilder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractRateLimiterFactory<CONF, CONFB extends ConfigBuilder<CONF>> {

    private final ConcurrentHashMap<String, CONF> configurations = new ConcurrentHashMap<>();

    /**
     * Adds configurations for circuit breakers.
     *
     * @param ids      The id of the circuit breaker
     * @param consumer A configuration builder consumer, allows consumers to customize the
     *                 builder before the configuration is built
     */
    public void configure(Consumer<CONFB> consumer, String... ids) {
        for (String id : ids) {
            CONFB builder = configBuilder(id);
            consumer.accept(builder);
            CONF conf = builder.build();
            getConfigurations().putIfAbsent(id, conf);
        }
    }

    /**
     * Gets the configurations for the circuit breakers.
     *
     * @return The configurations
     */
    protected ConcurrentHashMap<String, CONF> getConfigurations() {
        return configurations;
    }

    /**
     * Creates a configuration builder for the given id.
     *
     * @param id The id of the circuit breaker
     * @return The configuration builder
     */
    protected abstract CONFB configBuilder(String id);

    /**
     * Sets the default configuration for circuit breakers.
     *
     * @param defaultConfiguration A function that returns the default configuration
     */
    public abstract void configureDefault(Function<String, CONF> defaultConfiguration);

}
