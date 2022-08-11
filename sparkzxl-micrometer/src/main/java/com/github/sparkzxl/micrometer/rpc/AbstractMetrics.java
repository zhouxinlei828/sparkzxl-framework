package com.github.sparkzxl.micrometer.rpc;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-10 16:36:53
 */
public class AbstractMetrics {

    protected ConcurrentMap<String, Set<Meter.Id>> meterIdMap;

    void removeMetrics(MeterRegistry registry, String name) {
        Set<Meter.Id> ids = meterIdMap.get(name);
        if (ids != null) {
            ids.forEach(registry::remove);
        }
        meterIdMap.remove(name);
    }

    List<Tag> mapToTagsList(Map<String, String> tagsMap) {
        return tagsMap.entrySet()
                .stream().map(tagsEntry -> Tag.of(tagsEntry.getKey(), tagsEntry.getValue()))
                .collect(Collectors.toList());
    }


}
