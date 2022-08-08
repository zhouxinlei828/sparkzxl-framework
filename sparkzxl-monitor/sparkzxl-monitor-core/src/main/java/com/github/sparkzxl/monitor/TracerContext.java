package com.github.sparkzxl.monitor;

import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.LinkedList;

/**
 * description: TracerContext
 *
 * @author zhouxinlei
 * @since 2022-07-25 14:18:19
 */
public class TracerContext {
    private static final ThreadLocal<TracerContext> THREAD_LOCAL = new TransmittableThreadLocal<>();

    private final LinkedList<Object> spanList = new LinkedList<>();

    public static TracerContext getCurrentContext() {
        return THREAD_LOCAL.get();
    }

    public static void clearCurrentContext() {
        TracerContext tracerContext = THREAD_LOCAL.get();
        if (tracerContext == null) {
            return;
        }

        LinkedList<Object> spanList = tracerContext.getSpanList();
        if (!spanList.isEmpty()) {
            spanList.removeLast();
        }

        if (spanList.isEmpty()) {
            THREAD_LOCAL.remove();
        }
    }

    public Object getSpan() {
        if (spanList.isEmpty()) {
            return null;
        }

        return spanList.getLast();
    }

    public void setSpan(Object span) {
        spanList.addLast(span);
    }

    private LinkedList<Object> getSpanList() {
        return spanList;
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}