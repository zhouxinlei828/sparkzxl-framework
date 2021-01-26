package com.github.sparkzxl.jwt.service;

import com.fasterxml.jackson.core.io.JsonEOFException;
import com.github.sparkzxl.core.entity.JwtUserInfo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.RSAKey;

import java.io.Serializable;

/**
 * description: jwtToken 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:02:04
 */
public interface JwtTokenService<ID extends Serializable> {

    /**
     * 根据RSA算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     * @throws JOSEException 抛出异常
     */
    String createTokenByRsa(JwtUserInfo<ID> jwtUserInfo);

    /**
     * 根据RSA校验token
     *
     * @param token token
     * @return JwtUserInfo<ID>
     * @throws Exception 抛出异常
     */
    JwtUserInfo<ID> verifyTokenByRsa(String token) throws Exception;

    /**
     * 根据token获取信息
     *
     * @param token token信息
     * @return JwtUserInfo
     * @throws Exception 抛出异常
     */
    JwtUserInfo<ID> getJwtUserInfo(String token) throws Exception;

    /**
     * 获取jwt用户信息
     *
     * @param token token信息
     * @return JwtUserInfo<ID>
     * @throws Exception 抛出异常
     */
    JwtUserInfo<ID> getAuthJwtInfo(String token) throws Exception;

    /**
     * 根据HMAC算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     */
    String createTokenByHmac(JwtUserInfo<ID> jwtUserInfo);

    /**
     * 根据HMAC校验token
     *
     * @param token token
     * @return PayloadDto
     * @throws Exception 抛出异常
     */
    JwtUserInfo<ID> verifyTokenByHmac(String token) throws Exception;

    /**
     * 获取公钥
     *
     * @return RSAKey
     */
    RSAKey getRsaPublicKey();
}
