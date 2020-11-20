package com.github.sparkzxl.security.service;

import cn.hutool.core.date.DateUtil;
import com.github.sparkzxl.core.constant.BaseContextConstant;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.dto.LoginDTO;
import com.github.sparkzxl.security.entity.AuthToken;
import com.github.sparkzxl.security.entity.AuthUserDetail;
import com.github.sparkzxl.security.entity.LoginStatus;
import com.github.sparkzxl.security.event.LoginEvent;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Date;
import java.util.stream.Collectors;

/**
 * description: 登录授权Service
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:39:06
 */
@Slf4j
public abstract class AbstractSecurityLoginService {


    public JwtProperties jwtProperties;

    public JwtTokenService jwtTokenService;

    public UserDetailsService userDetailsService;

    @Autowired
    public void setJwtProperties(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Autowired
    public void setJwtTokenService(JwtTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 登录
     *
     * @param authRequest 登录认证
     * @return java.lang.String
     * @throws Exception 异常
     */
    public AuthToken login(LoginDTO authRequest) {
        String account = authRequest.getAccount();
        AuthUserDetail authUserDetail = (AuthUserDetail) userDetailsService.loadUserByUsername(account);
        ResponseResultStatus.ACCOUNT_EMPTY.assertNotNull(authUserDetail);
        //校验密码输入是否正确
        checkPasswordError(authRequest, authUserDetail);
        AuthToken authToken = authorization(authUserDetail);
        SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.success(authUserDetail.getId(), authUserDetail.getUsername())));
        return authToken;
    }

    /**
     * 授权登录获取token
     *
     * @param authUserDetail 授权用户
     * @return AuthToken
     */
    public AuthToken authorization(AuthUserDetail authUserDetail) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUserDetail,
                null, authUserDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthToken authToken = new AuthToken();
        authToken.setToken(createJwtToken(authUserDetail));
        authToken.setExpiration(jwtProperties.getExpire());
        authToken.setAuthUserDetail(authUserDetail);
        authToken.setTokenHead(BaseContextConstant.BEARER_TOKEN);
        //设置accessToken缓存
        accessToken(authToken, authUserDetail);
        return authToken;
    }


    private String createJwtToken(AuthUserDetail authUserDetail) {
        Date expire = DateUtil.offsetSecond(new Date(), jwtProperties.getExpire().intValue());
        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        jwtUserInfo.setSub(authUserDetail.getUsername());
        jwtUserInfo.setIat(System.currentTimeMillis());
        jwtUserInfo.setAuthorities(authUserDetail.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        jwtUserInfo.setUsername(authUserDetail.getUsername());
        jwtUserInfo.setExpire(expire);
        return jwtTokenService.createTokenByHmac(jwtUserInfo);
    }

    private void checkPasswordError(LoginDTO authRequest, AuthUserDetail authUserDetail) {
        String encryptPassword = HuSecretUtils.encryptMd5(authRequest.getPassword());
        log.info("密码加密 = {}，数据库密码={}", encryptPassword, authUserDetail.getPassword());
        //数据库密码比对
        boolean verifyResult = StringUtils.equals(encryptPassword, authUserDetail.getPassword());
        if (!verifyResult) {
            SpringContextUtils.publishEvent(new LoginEvent(LoginStatus.pwdError(authUserDetail.getId(),
                    ResponseResultStatus.PASSWORD_ERROR.getMessage())));
            ResponseResultStatus.PASSWORD_ERROR.assertNotTrue(false);
        }
    }

    /**
     * 设置accessToken缓存
     *
     * @param authToken      用户token
     * @param authUserDetail 认证用户
     */
    public abstract void accessToken(AuthToken authToken, AuthUserDetail authUserDetail);

}
