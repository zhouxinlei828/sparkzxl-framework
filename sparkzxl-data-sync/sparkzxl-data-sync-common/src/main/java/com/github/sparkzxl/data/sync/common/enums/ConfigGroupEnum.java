package com.github.sparkzxl.data.sync.common.enums;

import java.util.Arrays;
import java.util.Objects;

/**
 * description: configuration group.
 *
 * @author zhouxinlei
 * @since 2022-08-25 09:00:36
 */
public enum ConfigGroupEnum {

    /**
     * Meta data config group enum.
     */
    META_DATA("meta_data");

    private final String code;

    ConfigGroupEnum(String code) {
        this.code = code;
    }

    /**
     * Acquire by name config group enum.
     *
     * @param name the name
     * @return the config group enum
     */
    public static ConfigGroupEnum acquireByName(final String name) {
        return Arrays.stream(ConfigGroupEnum.values())
                .filter(e -> Objects.equals(e.name(), name))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format(" this ConfigGroupEnum can not support %s", name)));
    }

    /**
     * Acquire by code config group enum.
     *
     * @param code the code
     * @return the config group enum
     */
    public static ConfigGroupEnum acquireByCode(final String code) {
        return Arrays.stream(ConfigGroupEnum.values())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst().orElseThrow(() -> new RuntimeException(String.format(" this ConfigGroupEnum can not support %s", code)));
    }

    public String getCode() {
        return code;
    }
}
