package com.github.sparkzxl.oauth.server.entity;

import com.github.sparkzxl.core.entity.UserAgentEntity;
import com.github.sparkzxl.core.utils.UserAgentUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 登录态
 *
 * @author zuihou
 * @date 2020年03月18日17:25:44
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class LoginStatus implements Serializable {
    private static final long serialVersionUID = -3124612657759050173L;
    /***
     * 用户id
     */
    private Long id;
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

    public static LoginStatus success(Long id) {
        LoginStatus loginStatus = LoginStatus.builder()
                .id(id)
                .type(Type.SUCCESS).description("登录成功")
                .build().setInfo();
        return loginStatus;
    }

    public static LoginStatus success(String account) {
        LoginStatus loginStatus = LoginStatus.builder()
                .account(account)
                .type(Type.SUCCESS).description("登录成功")
                .build().setInfo();
        return loginStatus;
    }

    public static LoginStatus fail(Long id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.FAIL).description(description)
                .build().setInfo();
    }

    public static LoginStatus fail(String account, String description) {
        return LoginStatus.builder()
                .account(account)
                .type(Type.FAIL).description(description)
                .build().setInfo();
    }

    public static LoginStatus pwdError(Long id, String description) {
        return LoginStatus.builder()
                .id(id)
                .type(Type.PWD_ERROR).description(description)
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
        FAIL;
    }

}
