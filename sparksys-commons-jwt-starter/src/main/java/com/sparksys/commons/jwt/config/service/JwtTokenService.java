package com.sparksys.commons.jwt.config.service;

import com.nimbusds.jose.jwk.RSAKey;
import com.sparksys.commons.jwt.config.entity.JwtUserInfo;

/**
 * description: jwtToken 服务类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:02:04
 */
public interface JwtTokenService {

    /**
     * 根据RSA算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @param data        rsa加密
     * @return String
     */
    String createTokenByRsa(JwtUserInfo jwtUserInfo, String data);

    /**
     * 根据RSA校验token
     *
     * @param token token
     * @param data  rsa加密
     * @return PayloadDto
     */
    JwtUserInfo verifyTokenByRsa(String token, String data);

    /**
     * 根据HMAC算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @param secret     密钥
     * @return String
     */
    String createTokenByHmac(JwtUserInfo jwtUserInfo, String secret);

    /**
     * 根据HMAC校验token
     *
     * @param token  token
     * @param secret 密钥
     * @return PayloadDto
     */
    JwtUserInfo verifyTokenByHmac(String token, String secret);
}
