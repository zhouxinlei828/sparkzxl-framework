package com.github.sparkzxl.core.metrics;

import org.springframework.context.ApplicationEvent;

/***
 * description: MetricsEvent
 *
 * @author zhouxinlei
 * @since 2022-08-10 15:56:18
 */
public class MetricsEvent<T> extends ApplicationEvent {

    private final T entry;

    public MetricsEvent(T source) {
        super(source);
        this.entry = source;
    }

    @Override
    public T getSource() {
        return entry;
    }
}
