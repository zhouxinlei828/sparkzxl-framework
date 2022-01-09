package com.github.sparkzxl.gateway.constant.enums;

/**
 * description: 规则枚举类
 *
 * @author zhouxinlei
 * @date 2022-01-09 19:23:47
 */
public enum RuleEnum {

    RANDOM("random"),
    ;
    private final String name;

    RuleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
