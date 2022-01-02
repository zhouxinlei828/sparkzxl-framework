package com.github.sparkzxl.alarm.constant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * description: 消息类型枚举
 *
 * @author zhouxinlei
 * @date 2021-12-28 10:18:10
 */
@Getter
@RequiredArgsConstructor
public enum TemplateSource {
    JDBC,
    FILE,
    MEMORY;

}
