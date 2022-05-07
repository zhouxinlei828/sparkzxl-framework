package com.github.sparkzxl.oss.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 存储类型枚举
 *
 * @author zhouxinlei
 */
@Getter
@RequiredArgsConstructor
public enum StoreMode {
    /**
     * JDBC
     */
    JDBC,
    FILE,
    YAML

}
