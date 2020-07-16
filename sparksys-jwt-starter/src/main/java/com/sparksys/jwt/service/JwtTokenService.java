package com.sparksys.jwt.service;

import com.sparksys.jwt.entity.JwtUserInfo;

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
     * @return String
     */
    String createTokenByRsa(JwtUserInfo jwtUserInfo);

    /**
     * 根据RSA校验token
     *
     * @param token token
     * @return PayloadDto
     */
    JwtUserInfo verifyTokenByRsa(String token);

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
     * @param token  token
     * @return PayloadDto
     */
    JwtUserInfo verifyTokenByHmac(String token);
}
