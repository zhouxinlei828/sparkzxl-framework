package com.github.sparkzxl.jwt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.support.JwtInvalidException;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.properties.KeyStoreProperties;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
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
    public <T> String createTokenByRsa(JwtUserInfo<T> jwtUserInfo) {
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
    public <T> JwtUserInfo<T> verifyTokenByRsa(String token) {
        JwtUserInfo<T> jwtUserInfo = null;
        try {
            jwtUserInfo = getJwtUserInfo(token);
        } catch (ParseException e) {
            log.error("jwt转换异常：{}", ExceptionUtil.getMessage(e));
            e.printStackTrace();
        }
        assert jwtUserInfo != null;
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new JwtExpireException("token已过期");
        }
        return jwtUserInfo;
    }

    @Override
    public <T> JwtUserInfo<T> getJwtUserInfo(String token) throws ParseException {
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        return JSONUtil.toBean(payload, JwtUserInfo.class);
    }

    @Override
    public <T> JwtUserInfo<T> getAuthJwtInfo(String token) throws ParseException {
        JwtUserInfo<T> jwtUserInfo = new JwtUserInfo<T>();
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        T id = (T) jsonObject.get("id");
        jwtUserInfo.setId(id);
        String username = jsonObject.getStr("user_name");
        jwtUserInfo.setUsername(username);
        String name = jsonObject.getStr("name");
        jwtUserInfo.setName(name);
        String clientId = jsonObject.getStr("client_id");
        jwtUserInfo.setClientId(clientId);
        String sub = jsonObject.getStr("sub");
        jwtUserInfo.setSub(sub);
        Long iat = jsonObject.getLong("iat");
        jwtUserInfo.setIat(iat);
        Long exp = jsonObject.getLong("exp");
        if (ObjectUtils.isNotEmpty(exp)) {
            jwtUserInfo.setExpire(DateUtils.date(exp));
        }
        String jti = jsonObject.getStr("jti");
        jwtUserInfo.setJti(jti);
        List authorities = jsonObject.get("authorities", List.class);
        jwtUserInfo.setAuthorities(authorities);
        return jwtUserInfo;
    }

    @Override
    public <T> String createTokenByHmac(JwtUserInfo<T> jwtUserInfo) {
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
    public <T> JwtUserInfo<T> verifyTokenByHmac(String token) {

        try {
            JwtUserInfo<T> jwtUserInfo;
            //从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            //创建HMAC验证器
            JWSVerifier jwsVerifier = new MACVerifier(HuSecretUtils.encryptMd5(jwtProperties.getSecret()));
            if (!jwsObject.verify(jwsVerifier)) {
                throw new JwtInvalidException("token签名不合法");
            }
            String payload = jwsObject.getPayload().toString();
            jwtUserInfo = getJwtUserInfo(token);
            assert jwtUserInfo != null;
            if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
                throw new JwtExpireException("token已过期");
            }
            return jwtUserInfo;
        } catch (JOSEException | ParseException e) {
            log.warn("根据HMAC校验token失败：{}", e.getMessage());
            return null;
        }
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
