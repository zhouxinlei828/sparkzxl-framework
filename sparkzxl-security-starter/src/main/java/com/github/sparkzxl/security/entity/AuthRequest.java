package com.github.sparkzxl.security.entity;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * description: 登录请求
 *
 * @author zhouxinlei
 */
@Data
public class AuthRequest {

    @NotEmpty(message = "账户不能为空")
    private String username;

    @NotEmpty(message = "密码不能为空")
    private String password;

}
