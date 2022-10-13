package com.github.sparkzxl.core.metrics;

import org.springframework.context.ApplicationEventPublisher;

/**
 * description: Metrics Publisher
 *
 * @author zhouxinlei
 * @since 2022-08-10 16:00:37
 */
public class MetricsPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public MetricsPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }


    public void publishEvent(MetricsEvent event) {
        eventPublisher.publishEvent(event);
    }

}
