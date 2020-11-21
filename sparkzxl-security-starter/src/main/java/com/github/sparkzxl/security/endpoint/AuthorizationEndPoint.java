package com.github.sparkzxl.security.endpoint;

import com.github.sparkzxl.security.entity.AuthToken;
import com.github.sparkzxl.security.entity.AuthRequest;
import com.github.sparkzxl.security.service.AbstractSecurityLoginService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description: 授权登录管理
 *
 * @author: zhouxinlei
 * @date: 2020-11-21 17:12:35
 */
@AllArgsConstructor
@RestController
@RequestMapping("/authorization")
public class AuthorizationEndPoint {

    private final AbstractSecurityLoginService abstractSecurityLoginService;

    @PostMapping("/accessToken")
    public AuthToken getAccessToken(@RequestBody AuthRequest authRequest) {
        return abstractSecurityLoginService.login(authRequest);
    }

}
