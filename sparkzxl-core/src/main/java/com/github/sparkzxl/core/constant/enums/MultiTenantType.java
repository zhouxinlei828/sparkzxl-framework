package com.github.sparkzxl.core.constant.enums;

import lombok.Getter;

/**
 * description: 多租户类型 NONE、COLUMN、SCHEMA,DATASOURCE
 *
 * @author zhouxinlei
 * @since 2021-06-30 21:37:52
 */
@Getter
public enum MultiTenantType {
    /**
     * 非租户模式
     */
    NONE("非租户模式"),
    /**
     * 字段模式 在sql中拼接 tenant_code 字段
     */
    COLUMN("字段模式"),
    /**
     * 独立schema模式 在sql中拼接 数据库 schema
     */
    SCHEMA("独立schema模式"),
    /**
     * 独立数据源模式
     * <p>
     * 该模式不开源，购买咨询作者。
     */
    DATASOURCE("独立数据源模式"),
    ;
    private final String describe;


    MultiTenantType(String describe) {
        this.describe = describe;
    }

    public boolean eq(String val) {
        return this.name().equalsIgnoreCase(val);
    }

    public boolean eq(MultiTenantType val) {
        if (val == null) {
            return false;
        }
        return eq(val.name());
    }
}
