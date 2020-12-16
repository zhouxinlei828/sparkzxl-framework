package com.github.sparkzxl.security.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.entity.AuthRequest;
import com.github.sparkzxl.security.entity.AuthToken;
import com.github.sparkzxl.security.entity.AuthUserDetail;
import com.github.sparkzxl.security.entity.LoginStatus;
import com.github.sparkzxl.security.event.LoginEvent;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.openssl.PasswordException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.security.auth.login.AccountNotFoundException;
import java.io.Serializable;
import java.util.Date;

/**
 * description: 登录授权Service
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:39:06
 */
@Slf4j
public abstract class AbstractSecurityLoginService<ID extends Serializable> {

    /**
     * 登录
     *
     * @param authRequest 登录认证
     * @return java.lang.String
     */
    public AuthToken login(AuthRequest authRequest) throws AccountNotFoundException, PasswordException {
        String account = authRequest.getAccount();
        AuthUserDetail<ID> authUserDetail = (AuthUserDetail<ID>) getUserDetailsService().loadUserByUsername(account);
        if (ObjectUtils.isEmpty(authUserDetail)) {
            throw new AccountNotFoundException("账户不存在");
        }
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
    public AuthToken authorization(AuthUserDetail<ID> authUserDetail) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(authUserDetail,
                null, authUserDetail.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(createJwtToken(authUserDetail));
        authToken.setExpiration(getJwtProperties().getExpire());
        authToken.setAuthUserDetail(authUserDetail);
        authToken.setTokenType(BaseContextConstants.BEARER_TOKEN);
        //设置accessToken缓存
        accessToken(authToken, authUserDetail);
        return authToken;
    }


    private String createJwtToken(AuthUserDetail<ID> authUserDetail) {
        Date expire = DateUtil.offsetSecond(new Date(), getJwtProperties().getExpire().intValue());
        JwtUserInfo<ID> jwtUserInfo = new JwtUserInfo<>();
        jwtUserInfo.setId(authUserDetail.getId());
        jwtUserInfo.setName(authUserDetail.getName());
        jwtUserInfo.setUsername(authUserDetail.getUsername());
        jwtUserInfo.setSub(authUserDetail.getUsername());
        jwtUserInfo.setIat(System.currentTimeMillis());
        jwtUserInfo.setExpire(expire);
        jwtUserInfo.setAuthorities(authUserDetail.getAuthorityList());
        try {
            return getJwtTokenService().createTokenByHmac(jwtUserInfo);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("生成token发生异常：{}", ExceptionUtil.getMessage(e));
            SparkZxlExceptionAssert.businessFail("生成token发生异常：".concat(e.getMessage()));
            return null;
        }
    }

    /**
     * 校验密码正确性
     *
     * @param authRequest 登录请求
     * @param authUserDetail 用户信息
     */
    public abstract void checkPasswordError(AuthRequest authRequest, AuthUserDetail<ID> authUserDetail) throws PasswordException;


    /**
     * 设置accessToken缓存
     *
     * @param authToken      用户token
     * @param authUserDetail 认证用户
     */
    public abstract void accessToken(AuthToken authToken, AuthUserDetail<ID> authUserDetail);

    /**
     * 获取jwt配置属性
     *
     * @return JwtProperties
     */
    public abstract JwtProperties getJwtProperties();

    /**
     * 获取jwt service
     *
     * @return JwtTokenService
     */
    public abstract JwtTokenService<ID> getJwtTokenService();

    /**
     * 获取用户信息接口
     *
     * @return UserDetailsService
     */
    public abstract UserDetailsService getUserDetailsService();

}
