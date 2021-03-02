package com.github.sparkzxl.security.entity;

import com.github.sparkzxl.core.entity.UserAgentEntity;
import com.github.sparkzxl.core.utils.UserAgentUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * description: 登录态
 *
 * @author: zhouxinlei
 * @date: 2021-03-02 13:39:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoginStatus<T> implements Serializable {

    private static final long serialVersionUID = -3124612657759050173L;

    /***
     * 用户id
     */
    private T id;
    /**
     * 账号
     */
    private String account;

    /**
     * 登录类型
     */
    private Type type;
    /**
     * 登录描述
     */
    private String description;

    private UserAgentEntity userAgentEntity;

    public static <T> LoginStatus success(T id, String account) {
        LoginStatus loginStatus = LoginStatus.builder()
                .id(id)
                .account(account)
                .type(Type.SUCCESS)
                .description("登录成功")
                .build().setInfo();
        return loginStatus;
    }


    public static <T> LoginStatus fail(T id, String account, String description) {
        return LoginStatus.builder()
                .id(id)
                .account(account)
                .type(Type.FAIL)
                .description(description)
                .build().setInfo();
    }

    public static <T> LoginStatus pwdError(T id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.PWD_ERROR)
                .description(description)
                .build().setInfo();
    }

    private LoginStatus setInfo() {
        this.userAgentEntity = UserAgentUtils.getUserAgentEntity();
        return this;
    }

    @Getter
    public enum Type {
        /**
         * 登录态
         */
        SUCCESS,
        PWD_ERROR,
        FAIL
    }

}
