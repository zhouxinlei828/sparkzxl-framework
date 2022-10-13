package com.github.sparkzxl.micrometer.rpc.http;

import com.github.sparkzxl.core.metrics.MetricsEvent;

/**
 * description: http call metrics name
 *
 * @author zhouxinlei
 * @since 2022-08-10 16:38:13
 */
public class HttpCallMetricsEvent extends MetricsEvent<HttpCallData> {

    public HttpCallMetricsEvent(HttpCallData source) {
        super(source);
    }
}
