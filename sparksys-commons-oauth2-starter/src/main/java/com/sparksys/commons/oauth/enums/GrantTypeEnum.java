package com.sparksys.commons.oauth.enums;

import lombok.Getter;
import lombok.ToString;

/**
 * description： 授权类型枚举类
 *
 * @author： zhouxinlei
 * @date： 2020-06-24 15:27:05
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

    private String type;

    GrantTypeEnum(String type) {
        this.type = type;
    }
}
