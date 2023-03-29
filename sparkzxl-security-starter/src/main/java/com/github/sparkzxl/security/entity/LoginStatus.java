package com.github.sparkzxl.security.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * description: 登录态
 *
 * @author zhouxinlei
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

    public static <T> LoginStatus<?> success(T id, String account) {
        return LoginStatus.builder()
                .id(id)
                .account(account)
                .type(Type.SUCCESS)
                .description("登录成功")
                .build();
    }


    public static <T> LoginStatus<?> fail(T id, String account, String description) {
        return LoginStatus.builder()
                .id(id)
                .account(account)
                .type(Type.FAIL)
                .description(description)
                .build();
    }

    public static <T> LoginStatus<?> pwdError(T id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.PWD_ERROR)
                .description(description)
                .build();
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
