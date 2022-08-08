package com.github.sparkzxl.monitor;

import com.github.sparkzxl.monitor.constant.MonitorConstant;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 14:13:03
 */
public abstract class AbstractTracerExecutor<S> implements TracerExecutor {

    @Value("${" + StrategyConstant.SPRING_MONITOR_TRACER_ENABLED + ":false}")
    protected Boolean tracerEnabled;

    @Value("${" + StrategyConstant.SPRING_MONITOR_TRACER_SEPARATE_SPAN_ENABLED + ":true}")
    protected Boolean tracerSeparateSpanEnabled;

    @Value("${" + StrategyConstant.SPRING_MONITOR_TRACER_SPAN_VALUE + ":" + MonitorConstant.SPARKZXL_UPPERCASE + "}")
    protected String tracerSpanValue;

    @Override
    public void spanBuild() {
        if (!tracerEnabled) {
            return;
        }
        if (!tracerSeparateSpanEnabled) {
            return;
        }
        S span = buildSpan();
        TracerContext.getCurrentContext().setSpan(span);
    }

    @Override
    public void spanOutput(Map<String, String> contextMap) {
        if (!tracerEnabled) {
            return;
        }
        S span = getCurrentSpan();
        if (span == null) {
            return;
        }

        if (MapUtils.isNotEmpty(contextMap)) {
            for (Map.Entry<String, String> entry : contextMap.entrySet()) {
                outputSpan(span, entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void spanError(Throwable e) {
        if (!tracerEnabled) {
            return;
        }
        if (!tracerSeparateSpanEnabled) {
            return;
        }
        S span = getCurrentSpan();
        if (span == null) {
            return;
        }
        errorSpan(span, e);
    }

    @Override
    public void spanFinish() {
        if (!tracerEnabled) {
            return;
        }

        if (!tracerSeparateSpanEnabled) {
            return;
        }

        S span = getCurrentSpan();
        if (span != null) {
            finishSpan(span);
        } else {
            // LOG.error("Span not found in context to trace clear");
        }
        TracerContext.clearCurrentContext();
    }

    @Override
    public String getTraceId() {
        if (!tracerEnabled) {
            return null;
        }
        S span = getCurrentSpan();
        if (span != null) {
            return toTraceId(span);
        }
        return null;
    }

    @Override
    public String getSpanId() {
        if (!tracerEnabled) {
            return null;
        }
        S span = getCurrentSpan();
        if (span != null) {
            return toSpanId(span);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private S getCurrentSpan() {
        return tracerSeparateSpanEnabled ? (S) TracerContext.getCurrentContext().getSpan() : getActiveSpan();
    }

    protected abstract S buildSpan();

    protected abstract void outputSpan(S span, String key, String value);

    protected abstract void errorSpan(S span, Throwable e);

    protected abstract void finishSpan(S span);

    protected abstract S getActiveSpan();

    protected abstract String toTraceId(S span);

    protected abstract String toSpanId(S span);

}
