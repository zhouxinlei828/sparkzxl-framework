package com.github.sparkzxl.skywalking.monitor;

import com.github.sparkzxl.monitor.AbstractTracerExecutor;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import io.opentracing.Tracer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.skywalking.apm.toolkit.opentracing.SkywalkingTracer;
import org.springframework.beans.factory.annotation.Value;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:23:53
 */
public class SkyWalkingStrategyTracer extends AbstractTracerExecutor<SkyWalkingStrategySpan> {

    private final Tracer tracer = new SkywalkingTracer();
    @Value("${" + StrategyConstant.SPRING_MONITOR_TRACER_TRACER_EXCEPTION_DETAIL_OUTPUT_ENABLED + ":false}")
    protected Boolean tracerExceptionDetailOutputEnabled;

    @Override
    protected SkyWalkingStrategySpan buildSpan() {
        return new SkyWalkingStrategySpan(tracer.buildSpan(tracerSpanValue).startManual());
    }

    @Override
    protected void outputSpan(SkyWalkingStrategySpan span, String key, String value) {
        span.setTag(key, value);
    }

    @Override
    protected void errorSpan(SkyWalkingStrategySpan span, Throwable e) {
        if (tracerExceptionDetailOutputEnabled) {
            span.setTag(MonitorConstant.ERROR_OBJECT, ExceptionUtils.getStackTrace(e));
        } else {
            span.setTag(MonitorConstant.ERROR_OBJECT, e.getMessage());
        }
    }

    @Override
    protected void finishSpan(SkyWalkingStrategySpan span) {
        span.finish();
    }

    @Override
    protected SkyWalkingStrategySpan getActiveSpan() {
        return null;
    }

    @Override
    protected String toTraceId(SkyWalkingStrategySpan span) {
        return span.toTraceId();
    }

    @Override
    protected String toSpanId(SkyWalkingStrategySpan span) {
        return span.toSpanId();
    }
}
