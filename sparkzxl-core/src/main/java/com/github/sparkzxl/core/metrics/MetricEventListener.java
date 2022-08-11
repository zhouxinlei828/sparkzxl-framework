package com.github.sparkzxl.core.metrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;

/**
 * description: MetricEventListener
 *
 * @author zhouxinlei
 * @since 2022-08-10 15:57:40
 */
@Slf4j
public abstract class MetricEventListener<T extends MetricsEvent> implements ApplicationListener<T> {

    @Override
    public void onApplicationEvent(T event) {
        log.info("Listen to the event source, class [{}]", event.getSource().getClass().getName());
        addMetrics(event);
    }


    /**
     * 添加指标
     *
     * @param event 事件
     */
    protected abstract void addMetrics(T event);
}
