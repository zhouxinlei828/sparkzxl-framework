package com.github.sparkzxl.security.service;

import cn.hutool.core.date.DateUtil;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.entity.CaptchaInfo;
import com.github.sparkzxl.core.entity.JwtUserInfo;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.utils.TimeUtils;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.github.sparkzxl.security.entity.AuthRequest;
import com.github.sparkzxl.security.entity.AuthToken;
import com.github.sparkzxl.security.entity.AuthUserDetail;
import com.github.sparkzxl.security.entity.LoginStatus;
import com.github.sparkzxl.security.event.LoginEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
        long seconds = TimeUtils.toSeconds(getJwtProperties().getExpire(), getJwtProperties().getUnit());
        AuthToken authToken = new AuthToken();
        authToken.setAccessToken(createJwtToken(authUserDetail));
        authToken.setExpiration(seconds);
        authToken.setAuthUserDetail(authUserDetail);
        authToken.setTokenType(BaseContextConstants.BEARER_TOKEN);
        //设置accessToken缓存
        accessToken(authToken, authUserDetail);
        return authToken;
    }


    public String createJwtToken(AuthUserDetail<ID> authUserDetail) {
        long seconds = TimeUtils.toSeconds(getJwtProperties().getExpire(), getJwtProperties().getUnit());
        Date expire = DateUtil.offsetSecond(new Date(), (int) seconds);
        JwtUserInfo<ID> jwtUserInfo = new JwtUserInfo<>();
        jwtUserInfo.setId(authUserDetail.getId());
        jwtUserInfo.setName(authUserDetail.getName());
        jwtUserInfo.setUsername(authUserDetail.getUsername());
        jwtUserInfo.setSub(authUserDetail.getUsername());
        jwtUserInfo.setIat(System.currentTimeMillis());
        jwtUserInfo.setExpire(expire);
        jwtUserInfo.setAuthorities(authUserDetail.getAuthorityList());
        return getJwtTokenService().createTokenByHmac(jwtUserInfo);
    }

    /**
     * 校验密码正确性
     *
     * @param authRequest    登录请求
     * @param authUserDetail 用户信息
     * @throws PasswordException 密码校验异常
     */
    public abstract void checkPasswordError(AuthRequest authRequest, AuthUserDetail<ID> authUserDetail) throws PasswordException;


    /**
     * 生成验证码
     *
     * @param type 验证码类型
     * @return CaptchaInfo
     */
    public CaptchaInfo createCaptcha(String type) {
        return null;
    }

    /**
     * 校验验证码
     *
     * @param key   前端上送 key
     * @param value 前端上送待校验值
     * @return 是否成功
     */
    public boolean checkCaptcha(String key, String value) {
        return false;
    }

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
