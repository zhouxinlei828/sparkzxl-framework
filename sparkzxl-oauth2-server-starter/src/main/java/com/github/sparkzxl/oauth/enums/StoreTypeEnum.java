package com.github.sparkzxl.oauth.enums;

import lombok.Getter;
import lombok.ToString;

/**
 * description： 授权类型枚举类
 *
 * @author zhouxinlei
 */
@Getter
@ToString
public enum StoreTypeEnum {
    /**
     * 内存存储
     */
    MEMORY("memory"),
    /**
     * 数据库存储
     */
    DATABASE("database");

    private final String type;

    StoreTypeEnum(String type) {
        this.type = type;
    }
}
