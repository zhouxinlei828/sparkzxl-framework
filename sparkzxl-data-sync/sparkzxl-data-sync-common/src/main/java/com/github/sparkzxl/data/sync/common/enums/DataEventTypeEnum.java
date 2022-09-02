package com.github.sparkzxl.data.sync.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * description: The enum Data event type.
 *
 * @author zhouxinlei
 * @since 2022-08-24 15:41:42
 */
public enum DataEventTypeEnum {

    /**
     * delete event.
     */
    DELETE,

    /**
     * insert event.
     */
    CREATE,

    /**
     * update event.
     */
    UPDATE,

    /**
     * REFRESH data event type enum.
     */
    REFRESH,

    /**
     * Myself data event type enum.
     */
    MYSELF;

    /**
     * Acquire by name data event type enum.
     *
     * @param name the name
     * @return the data event type enum
     */
    public static DataEventTypeEnum acquireByName(final String name) {
        return Arrays.stream(DataEventTypeEnum.values())
                .filter(e -> Objects.equals(e.name(), name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format(" this DataEventTypeEnum can not support %s", name)));
    }
}
