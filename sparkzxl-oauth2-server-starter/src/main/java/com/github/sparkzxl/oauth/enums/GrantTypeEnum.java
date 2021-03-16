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
public enum GrantTypeEnum {
    /**
     * 密码
     */
    PASSWORD("password"),
    /**
     * 授权码
     */
    AUTHORIZATION_CODE("authorization_code"),
    /**
     * 刷新token
     */
    REFRESH_TOKEN("refresh_token");

    private final String type;

    GrantTypeEnum(String type) {
        this.type = type;
    }
}
