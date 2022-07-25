package com.github.sparkzxl.opentracing.monitor;

import com.github.sparkzxl.monitor.AbstractTracerExecutor;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import io.opentracing.Span;
import io.opentracing.Tracer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:13:47
 */
public class OpenTracingTracerExecutor extends AbstractTracerExecutor<Span> {

    @Value("${" + StrategyConstant.SPRING_MONITOR_TRACER_TRACER_EXCEPTION_DETAIL_OUTPUT_ENABLED + ":false}")
    protected Boolean tracerExceptionDetailOutputEnabled;

    @Autowired
    private Tracer tracer;

    @Override
    protected Span buildSpan() {
        return tracer.buildSpan(tracerSpanValue).start();
    }

    @Override
    protected void outputSpan(Span span, String key, String value) {
        span.setTag(key, value);
    }

    @Override
    protected void errorSpan(Span span, Throwable e) {
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(MonitorConstant.EVENT, MonitorConstant.ERROR);
        if (tracerExceptionDetailOutputEnabled) {
            map.put(MonitorConstant.ERROR_OBJECT, ExceptionUtils.getStackTrace(e));
        } else {
            map.put(MonitorConstant.ERROR_OBJECT, e);
        }
        span.log(map);
    }

    @Override
    protected void finishSpan(Span span) {
        span.finish();
    }

    @Override
    protected Span getActiveSpan() {
        return tracer.activeSpan();
    }

    @Override
    protected String toTraceId(Span span) {
        return span.context().toTraceId();
    }

    @Override
    protected String toSpanId(Span span) {
        return span.context().toSpanId();
    }
}
