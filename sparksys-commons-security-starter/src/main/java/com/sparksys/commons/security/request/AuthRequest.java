package com.sparksys.commons.security.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * description: 登录请求参数
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:55
 */
@Data
public class AuthRequest {

    @NotEmpty(message = "账户不能为空")
    private String account;

    @NotEmpty(message = "密码不能为空")
    private String password;

}
