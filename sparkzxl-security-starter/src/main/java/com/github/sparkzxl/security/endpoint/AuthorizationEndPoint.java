package com.github.sparkzxl.security.endpoint;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.security.entity.AuthToken;
import com.github.sparkzxl.security.entity.AuthRequest;
import com.github.sparkzxl.security.service.AbstractSecurityLoginService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;

/**
 * description: 授权登录管理
 *
 * @author: zhouxinlei
 * @date: 2020-11-21 17:12:35
 */
@AllArgsConstructor
@RestController
@RequestMapping("/authorization")
@Slf4j
public class AuthorizationEndPoint {

    private final AbstractSecurityLoginService abstractSecurityLoginService;

    @PostMapping("/accessToken")
    public ApiResult<AuthToken> getAccessToken(@RequestBody AuthRequest authRequest) {
        try {
            return ApiResult.apiResult(200, "登录成功", abstractSecurityLoginService.login(authRequest));
        } catch (AccountNotFoundException | PasswordException e) {
            log.error("登陆失败 exception：{}", ExceptionUtil.getMessage(e));
            return ApiResult.apiResult(500, e.getMessage());
        }
    }

}
