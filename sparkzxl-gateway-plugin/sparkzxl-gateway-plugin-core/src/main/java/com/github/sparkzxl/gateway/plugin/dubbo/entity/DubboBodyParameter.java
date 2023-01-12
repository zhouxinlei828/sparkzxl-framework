package com.github.sparkzxl.gateway.plugin.dubbo.entity;

/**
 * description: dubbo body parameters
 *
 * @author zhouxinlei
 * @since 2023-01-11 17:05:51
 */
public class DubboBodyParameter {

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
