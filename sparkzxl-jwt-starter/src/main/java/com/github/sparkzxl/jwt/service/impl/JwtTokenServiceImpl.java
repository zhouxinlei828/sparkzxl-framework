package com.github.sparkzxl.jwt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.properties.KeyStoreProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.github.sparkzxl.core.support.ResponseResultStatus;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.Map;

/**
 * description: jwtToken 服务实现类
 *
 * @author: zhouxinlei
 * @date: 2020-07-14 08:03:30
 */
@Slf4j
@AllArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtProperties jwtProperties;
    private final KeyStoreProperties KeyStoreProperties;
    private Map<String, KeyPair> keyPairMap;

    @Override
    public String createTokenByRsa(JwtUserInfo jwtUserInfo) {
        //创建JWS头，设置签名算法和类型
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                .type(JOSEObjectType.JWT)
                .build();
        Date expire = DateUtil.offsetSecond(new Date(), jwtProperties.getExpire().intValue());
        jwtUserInfo.setExpire(expire);
        jwtUserInfo.setJti(UUID.randomUUID().toString());
        //将负载信息封装到Payload中
        String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
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
            SparkZxlExceptionAssert.businessFail(e.getMessage());
        }
        return jwsObject.serialize();
    }

    @Override
    public JwtUserInfo verifyTokenByRsa(String token) {
        try {
            //从token中解析JWS对象
            JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
            assert jwtUserInfo != null;
            ResponseResultStatus.JWT_EXPIRED_ERROR.assertCompare(jwtUserInfo.getExpire().getTime(), System.currentTimeMillis());
            return jwtUserInfo;
        } catch (Exception e) {
            log.warn("根据RSA校验token失败：{}", e.getMessage());
            SparkZxlExceptionAssert.businessFail(e.getMessage());
        }
        return null;
    }

    @Override
    public JwtUserInfo getJwtUserInfo(String token) {
        try {
            JwtUserInfo jwtUserInfo = new JwtUserInfo();
            JWSObject jwsObject = JWSObject.parse(token);
            RSAKey publicRsaKey = getRsaKey().toPublicJWK();
            JWSVerifier jwsVerifier = new RSASSAVerifier(publicRsaKey);
            ResponseResultStatus.JWT_VALID_ERROR.assertNotTrue(jwsObject.verify(jwsVerifier));
            String payload = jwsObject.getPayload().toString();
            JSONObject jsonObject = JSONUtil.parseObj(payload);
            Long id = jsonObject.getLong("id");
            String sub = jsonObject.getStr("sub");
            String username = jsonObject.getStr("user_name");
            Long exp = jsonObject.getLong("exp");
            Long iat = jsonObject.getLong("iat");
            jwtUserInfo.setId(id);
            jwtUserInfo.setSub(sub);
            jwtUserInfo.setUsername(username);
            jwtUserInfo.setIat(iat);
            if (ObjectUtils.isNotEmpty(exp)){
                jwtUserInfo.setExpire(DateUtils.date(exp));
            }
            return jwtUserInfo;
        } catch (Exception e) {
            log.warn("根据RSA校验token失败：{}", e.getMessage());
        }
        return null;
    }


    @Override
    public String createTokenByHmac(JwtUserInfo jwtUserInfo) {
        //创建JWS头，设置签名算法和类型
        JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).
                type(JOSEObjectType.JWT)
                .build();
        //将负载信息封装到Payload中
        String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
        Payload payload = new Payload(payloadStr);
        //创建JWS对象
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        try {
            //创建HMAC签名器
            JWSSigner jwsSigner = new MACSigner(HuSecretUtils.encryptMd5(jwtProperties.getSecret()));
            //签名
            jwsObject.sign(jwsSigner);
        } catch (Exception e) {
            log.warn("根据HMAC算法生成token失败：{}", e.getMessage());
            SparkZxlExceptionAssert.businessFail(e.getMessage());
        }
        return jwsObject.serialize();
    }

    @Override
    public JwtUserInfo verifyTokenByHmac(String token) {
        JwtUserInfo payloadDto = null;
        try {
            //从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            //创建HMAC验证器
            JWSVerifier jwsVerifier = new MACVerifier(HuSecretUtils.encryptMd5(jwtProperties.getSecret()));
            ResponseResultStatus.JWT_VALID_ERROR.assertNotTrue(jwsObject.verify(jwsVerifier));
            String payload = jwsObject.getPayload().toString();
            payloadDto = JSONUtil.toBean(payload, JwtUserInfo.class);
            ResponseResultStatus.JWT_EXPIRED_ERROR.assertCompare(payloadDto.getExpire().getTime(), System.currentTimeMillis());
        } catch (Exception e) {
            log.warn("根据HMAC校验token失败：{}", e.getMessage());
            SparkZxlExceptionAssert.businessFail(e.getMessage());
        }
        return payloadDto;
    }

    private KeyPair getKeyPair() {
        KeyPair keyPair = keyPairMap.get("keyPair");
        if (ObjectUtils.isNotEmpty(keyPair)) {
            return keyPair;
        }
        keyPair = HuSecretUtils.keyPair(KeyStoreProperties.getPath(), "jwt", KeyStoreProperties.getPassword());
        keyPairMap.put("keyPair", keyPair);
        return keyPair;
    }

    private RSAKey getRsaKey() {
        KeyPair keyPair = getKeyPair();
        //获取RSA公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        //获取RSA私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).build();
    }

    @Override
    public RSAKey getRsaPublicKey() {
        //获取RSA公钥
        RSAPublicKey publicKey = (RSAPublicKey) getKeyPair().getPublic();
        return new RSAKey.Builder(publicKey).build();
    }
}
