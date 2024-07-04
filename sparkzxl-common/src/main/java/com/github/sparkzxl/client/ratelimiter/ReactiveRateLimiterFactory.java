package com.github.sparkzxl.client.ratelimiter;

import com.github.sparkzxl.client.ConfigBuilder;

public abstract class ReactiveRateLimiterFactory<CONF, CONFB extends ConfigBuilder<CONF>>
        extends AbstractRateLimiterFactory<CONF, CONFB> {

    public abstract ReactiveRateLimiter create(String id);

    public ReactiveRateLimiter create(String id, String groupName) {
        return create(id);
    }

}

