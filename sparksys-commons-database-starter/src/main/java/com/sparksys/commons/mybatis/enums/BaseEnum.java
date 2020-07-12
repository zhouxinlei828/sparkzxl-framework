package com.sparksys.commons.mybatis.enums;

import com.baomidou.mybatisplus.core.enums.IEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * description: 枚举基类
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:22:10
 */
public interface BaseEnum extends IEnum<String> {

    default String getCode() {
        return this.toString();
    }

    String getDesc();

    @Override
    @JsonIgnore
    default String getValue() {
        return this.getCode();
    }
}

