package com.github.sparkzxl.monitor;

import java.util.Map;

/**
 * description: 追踪器适配器
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:50:44
 */
public interface TracerAdapter {

    String getTraceId();

    String getSpanId();

    Map<String, String> getCustomizationMap();

}
