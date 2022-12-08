package com.github.sparkzxl.mybatis.constant;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * description: SQL条件类型
 *
 * @author zhouxinlei
 * @since 2022-11-09 18:44:24
 */
@AllArgsConstructor
public enum SqlConditions implements ISqlSegment {

    EQ("="),
    IN("IN"),
    LIKE("LIKE"),
    LIST_IN("LIST_IN"),
    ;

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }

}
