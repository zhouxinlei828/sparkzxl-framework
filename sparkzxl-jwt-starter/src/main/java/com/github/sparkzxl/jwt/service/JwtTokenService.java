package com.github.sparkzxl.jwt.service;

import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.nimbusds.jose.jwk.RSAKey;

import java.io.Serializable;

/**
 * description: jwtToken 服务类
 *
 * @author zhouxinlei
 */
public interface JwtTokenService {

    /**
     * 根据RSA算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     */
    String createTokenByRsa(JwtUserInfo jwtUserInfo);

    /**
     * 根据RSA校验token
     *
     * @param token token
     * @return JwtUserInfo
     * @throws Exception 抛出异常
     */
    JwtUserInfo verifyTokenByRsa(String token) throws Exception;

    /**
     * 根据token获取信息
     *
     * @param token token信息
     * @return JwtUserInfo
     * @throws Exception 抛出异常
     */
    JwtUserInfo getJwtUserInfo(String token) throws Exception;

    /**
     * 获取jwt用户信息
     *
     * @param token token信息
     * @return JwtUserInfo
     * @throws Exception 抛出异常
     */
    JwtUserInfo getAuthJwtInfo(String token) throws Exception;

    /**
     * 根据HMAC算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     */
    String createTokenByHmac(JwtUserInfo jwtUserInfo);

    /**
     * 根据HMAC校验token
     *
     * @param token token
     * @return JwtUserInfo
     * @throws Exception 抛出异常
     */
    JwtUserInfo verifyTokenByHmac(String token) throws Exception;

    /**
     * 获取公钥
     *
     * @return RSAKey
     */
    RSAKey getRsaPublicKey();
}
