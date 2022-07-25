package com.github.sparkzxl.skywalking.monitor;

import com.github.sparkzxl.monitor.StrategySpan;
import com.github.sparkzxl.monitor.TracerContext;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import io.opentracing.Span;
import io.opentracing.SpanContext;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:24:03
 */
public class SkyWalkingStrategySpan implements Span {

    private final Span span;
    private final String traceId;
    private final String spanId;

    public SkyWalkingStrategySpan(Span span) {
        this.span = span;
        this.traceId = createTraceId();
        this.spanId = createSpanId();
    }

    @Override
    public Span setOperationName(String operationName) {
        return span.setOperationName(operationName);
    }

    @Override
    public Span log(long timestampMicroseconds, Map<String, ?> fields) {
        return span.log(timestampMicroseconds, fields);
    }

    @Override
    public void finish(long finishMicros) {
        span.finish(finishMicros);
    }

    @Override
    public Span log(long timestampMicroseconds, String event) {
        return span.log(timestampMicroseconds, event);
    }

    @Override
    public void finish() {
        span.finish();
    }

    @Override
    public SpanContext context() {
        return span.context();
    }

    @Override
    public Span setTag(String key, String value) {
        return span.setTag(key, value);
    }

    @Override
    public Span setTag(String key, boolean value) {
        return span.setTag(key, value);
    }

    @Override
    public Span setTag(String key, Number value) {
        return span.setTag(key, value);
    }

    @Override
    public Span log(Map<String, ?> fields) {
        return span.log(fields);
    }

    @Override
    public Span log(String event) {
        return span.log(event);
    }

    @Override
    public Span setBaggageItem(String key, String value) {
        return span.setBaggageItem(key, value);
    }

    @Override
    public String getBaggageItem(String key) {
        return span.getBaggageItem(key);
    }

    @Deprecated
    @Override
    public Span log(String eventName, Object payload) {
        return span.log(eventName, payload);
    }

    @Deprecated
    @Override
    public Span log(long timestampMicroseconds, String eventName, Object payload) {
        return span.log(timestampMicroseconds, eventName, payload);
    }

    public String toTraceId() {
        return traceId;
    }

    public String toSpanId() {
        return spanId;
    }

    private String createTraceId() {
        try {
            Object span = TracerContext.getCurrentContext().getSpan();
            if (span != null) {
                // 该方式适用在WebFlux模式下
                StrategySpan strategySpan = (StrategySpan) span;
                return strategySpan.getTraceId();
            } else {
                // 该方式适用在WebMvc模式下
                return TraceContext.traceId();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String createSpanId() {
        try {
            return MonitorConstant.IGNORED;
        } catch (Exception e) {
            return null;
        }
    }
}
