package com.sparksys.commons.core.utils.jwt;

import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.sparksys.commons.core.support.ResponseResultStatus;
import com.sparksys.commons.core.support.SparkSysExceptionAssert;
import com.sparksys.commons.core.utils.crypto.KeyPairUtils;
import com.sparksys.commons.core.utils.crypto.MD5Utils;
import com.sparksys.commons.core.entity.JwtUserInfo;
import lombok.extern.slf4j.Slf4j;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * description: jwtToken 工具类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:02:04
 */
@Slf4j
public class JwtTokenUtils {

    /**
     * 根据RSA算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     */
    public static String createTokenByRsa(JwtUserInfo jwtUserInfo) {
        //创建JWS头，设置签名算法和类型
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
        jwtUserInfo.setJti(UUID.fromString(jwtUserInfo.getUsername()).toString());
        //将负载信息封装到Payload中
        String payloadStr = JSONUtil.toJsonPrettyStr(jwtUserInfo);
        Payload payload = new Payload(payloadStr);
        //创建JWS对象
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        //创建RSA签名器
        JWSSigner jwsSigner;
        try {
            jwsSigner = new RSASSASigner(getRsaKey(), true);
            //签名
            jwsObject.sign(jwsSigner);
        } catch (JOSEException e) {
            log.warn("根据RSA算法生成token失败：{}", e.getMessage());
            SparkSysExceptionAssert.businessFail(e.getMessage());
        }
        return jwsObject.serialize();
    }

    /**
     * 根据RSA校验token
     *
     * @param token token
     * @return PayloadDto
     */
    public static JwtUserInfo verifyTokenByRsa(String token) {
        JwtUserInfo payloadDto = null;
        try {
            //从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            RSAKey publicRsaKey = getRsaKey().toPublicJWK();
            //使用RSA公钥创建RSA验证器
            JWSVerifier jwsVerifier = new RSASSAVerifier(publicRsaKey);
            ResponseResultStatus.JWT_VALID_ERROR.assertNotTrue(jwsObject.verify(jwsVerifier));
            String payload = jwsObject.getPayload().toString();
            payloadDto = JSONUtil.toBean(payload, JwtUserInfo.class);
            ResponseResultStatus.JWT_VALID_ERROR.assertCompare(payloadDto.getExpire(), System.currentTimeMillis());
            return payloadDto;
        } catch (Exception e) {
            log.warn("根据RSA校验token失败：{}", e.getMessage());
            SparkSysExceptionAssert.businessFail(e.getMessage());
        }
        return payloadDto;
    }

    /**
     * 根据HMAC算法生成token
     *
     * @param jwtUserInfo 负载信息
     * @return String
     */
    public static String createTokenByHmac(JwtUserInfo jwtUserInfo, String secret) {
        //创建JWS头，设置签名算法和类型
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).
                type(JOSEObjectType.JWT)
                .build();
        //将负载信息封装到Payload中
        String payloadStr = JSONUtil.toJsonPrettyStr(jwtUserInfo);
        Payload payload = new Payload(payloadStr);
        //创建JWS对象
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            //创建HMAC签名器
            JWSSigner jwsSigner = new MACSigner(MD5Utils.encrypt(secret));
            //签名
            jwsObject.sign(jwsSigner);
        } catch (Exception e) {
            log.warn("根据HMAC算法生成token失败：{}", e.getMessage());
            SparkSysExceptionAssert.businessFail(e.getMessage());
        }
        return jwsObject.serialize();
    }

    /**
     * 根据HMAC校验token
     *
     * @param token token
     * @return PayloadDto
     */
    public static JwtUserInfo verifyTokenByHmac(String token, String secret) {
        JwtUserInfo payloadDto = null;
        try {
            //从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            //创建HMAC验证器
            JWSVerifier jwsVerifier = new MACVerifier(MD5Utils.encrypt(secret));
            ResponseResultStatus.JWT_VALID_ERROR.assertNotTrue(jwsObject.verify(jwsVerifier));
            String payload = jwsObject.getPayload().toString();
            payloadDto = JSONUtil.toBean(payload, JwtUserInfo.class);
            ResponseResultStatus.JWT_VALID_ERROR.assertCompare(payloadDto.getExpire(), System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("根据HMAC校验token失败：{}", e.getMessage());
            SparkSysExceptionAssert.businessFail(e.getMessage());
        }
        return payloadDto;
    }

    protected static RSAKey getRsaKey() {
        //获取RSA公钥
        RSAPublicKey publicKey = (RSAPublicKey) KeyPairUtils.keyPair("jwt.jks", "123456").getPublic();
        //获取RSA私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) KeyPairUtils.keyPair("jwt.jks", "123456").getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).build();
    }
}
