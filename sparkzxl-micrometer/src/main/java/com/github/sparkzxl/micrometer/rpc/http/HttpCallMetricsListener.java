package com.github.sparkzxl.micrometer.rpc.http;

import com.github.sparkzxl.core.metrics.MetricEventListener;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-10 17:00:28
 */
@Component
public class HttpCallMetricsListener extends MetricEventListener<HttpCallMetricsEvent> {

    @Override
    protected void addMetrics(HttpCallMetricsEvent event) {
        HttpCallData httpCallData = event.getSource();
    }
}
