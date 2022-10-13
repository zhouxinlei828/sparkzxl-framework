package com.github.sparkzxl.oss.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 注册类型枚举
 *
 * @author zhouxinlei
 */
@Getter
@RequiredArgsConstructor
public enum RegisterMode {
    /**
     * JDBC
     */
    JDBC,
    FILE,
    YAML

}
