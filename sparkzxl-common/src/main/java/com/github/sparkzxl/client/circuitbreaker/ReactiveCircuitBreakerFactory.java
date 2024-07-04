package com.github.sparkzxl.client.circuitbreaker;

import com.github.sparkzxl.client.ConfigBuilder;

/**
 * description: Creates reactive circuit breakers.
 *
 * @author zhouxinlei
 * @since 2024-07-01 15:24:25
 */
public abstract class ReactiveCircuitBreakerFactory<CONF, CONFB extends ConfigBuilder<CONF>>
        extends AbstractCircuitBreakerFactory<CONF, CONFB> {

    public abstract ReactiveCircuitBreaker create(String id);

    public ReactiveCircuitBreaker create(String id, String groupName) {
        return create(id);
    }

}
