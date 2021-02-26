package com.github.sparkzxl.open.service;

import com.github.sparkzxl.core.entity.CaptchaInfo;
import com.github.sparkzxl.open.entity.AuthorizationRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.security.Principal;

/**
 * description: Oauth认证 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-06-25 09:49:22
 */
public interface OauthService {

    String OAUTH_AUTHORIZE_URL = "oauth/authorize?client_id={}&redirect_uri={}&response_type=code&state={}";

    /**
     * get请求授权登录
     *
     * @param principal            认证主体
     * @param authorizationRequest 认证请求
     * @return OAuth2AccessToken
     * @throws HttpRequestMethodNotSupportedException 请求方法异常
     */
    OAuth2AccessToken getAccessToken(Principal principal, AuthorizationRequest authorizationRequest) throws HttpRequestMethodNotSupportedException;

    /**
     * POST请求授权登录
     *
     * @param principal            认证主体
     * @param authorizationRequest 认证请求
     * @return OAuth2AccessToken
     * @throws HttpRequestMethodNotSupportedException 请求方法异常
     */
    OAuth2AccessToken postAccessToken(Principal principal, AuthorizationRequest authorizationRequest) throws HttpRequestMethodNotSupportedException;

    /**
     * 生成验证码
     *
     * @param type 验证码类型
     * @return CaptchaInfo
     */
    CaptchaInfo createCaptcha(String type);

    /**
     * 校验验证码
     *
     * @param key   前端上送 key
     * @param value 前端上送待校验值
     * @return 是否成功
     */
    boolean checkCaptcha(String key, String value);

    /**
     * 获取授权认证连接
     *
     * @param frontUrl 前端地址
     * @return String
     */
    String getAuthorizeUrl(String frontUrl);


    /**
     * 授权回调
     *
     * @param authorizationCode 授权码
     * @param loginState        登录态
     * @return OAuth2AccessToken
     */
    OAuth2AccessToken callBack(String authorizationCode, String loginState);

}
