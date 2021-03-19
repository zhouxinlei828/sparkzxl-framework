package com.github.sparkzxl.jwt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.entity.JwtUserInfo;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.support.JwtInvalidException;
import com.github.sparkzxl.core.support.SparkZxlExceptionAssert;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.core.utils.HuSecretUtils;
import com.github.sparkzxl.core.utils.TimeUtils;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.properties.KeyStoreProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.io.Serializable;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: jwtToken 服务实现类
 *
 * @author zhouxinlei
 */
@Slf4j
public class JwtTokenServiceImpl<ID extends Serializable> implements JwtTokenService<ID> {

    private final JwtProperties jwtProperties;
    private final KeyStoreProperties KeyStoreProperties;
    private Map<String, KeyPair> keyPairMap;

    public JwtTokenServiceImpl(JwtProperties jwtProperties, KeyStoreProperties keyStoreProperties) {
        this.jwtProperties = jwtProperties;
        this.KeyStoreProperties = keyStoreProperties;
    }

    public void setKeyPairMap(Map<String, KeyPair> keyPairMap) {
        this.keyPairMap = keyPairMap;
    }

    public Map<String, KeyPair> getKeyPairMap() {
        return keyPairMap;
    }

    @Override
    public String createTokenByRsa(JwtUserInfo<ID> jwtUserInfo) {
        return Try.of(() -> {
            //创建JWS头，设置签名算法和类型
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            long seconds = TimeUtils.toSeconds(jwtProperties.getExpire(), jwtProperties.getUnit());
            Date expire = DateUtil.offsetSecond(new Date(), (int) seconds);
            jwtUserInfo.setExpire(expire);
            jwtUserInfo.setJti(UUID.randomUUID().toString());
            //将负载信息封装到Payload中
            String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
            Payload payload = new Payload(payloadStr);
            //创建JWS对象
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            //创建RSA签名器
            JWSSigner jwsSigner = new RSASSASigner(getRsaKey());
            //签名
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        }).onFailure(throwable -> {
            log.error("根据RSA算法生成token发生异常：{}", ExceptionUtil.getSimpleMessage(throwable));
            SparkZxlExceptionAssert.businessFail("生成token发生异常：".concat(throwable.getMessage()));
        }).getOrElse("");
    }

    @Override
    public JwtUserInfo<ID> verifyTokenByRsa(String token) throws Exception {
        JwtUserInfo<ID> jwtUserInfo = getJwtUserInfo(token);
        assert jwtUserInfo != null;
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new JwtExpireException("token已过期");
        }
        return jwtUserInfo;
    }

    @Override
    public JwtUserInfo<ID> getJwtUserInfo(String token) throws Exception {
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        return JSONUtil.toBean(payload, JwtUserInfo.class);
    }

    @Override
    public JwtUserInfo<ID> getAuthJwtInfo(String token) throws Exception {
        JwtUserInfo<ID> jwtUserInfo = new JwtUserInfo<>();
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        ID id = (ID) jsonObject.get("id");
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
            jwtUserInfo.setExpire(DateUtils.date(exp * 1000));
        }
        String jti = jsonObject.getStr("jti");
        jwtUserInfo.setJti(jti);
        String realm = jsonObject.getStr("realm");
        jwtUserInfo.setRealm(realm);
        List authorities = jsonObject.get("authorities", List.class);
        jwtUserInfo.setAuthorities(authorities);
        return jwtUserInfo;
    }

    @Override
    public String createTokenByHmac(JwtUserInfo<ID> jwtUserInfo) {
        return Try.of(() -> {
            //创建JWS头，设置签名算法和类型
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).
                    type(JOSEObjectType.JWT)
                    .build();
            //将负载信息封装到Payload中
            String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
            Payload payload = new Payload(payloadStr);
            //创建JWS对象
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            JWSSigner jwsSigner = new MACSigner(HuSecretUtils.encryptMd5(jwtProperties.getSecret()));
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        }).onFailure(throwable -> {
            log.error("根据HMAC算法生成token发生异常：{}", ExceptionUtil.getSimpleMessage(throwable));
            SparkZxlExceptionAssert.businessFail("生成token发生异常：".concat(throwable.getMessage()));

        }).getOrElse("");
    }

    @Override
    public JwtUserInfo<ID> verifyTokenByHmac(String token) throws Exception {
        JwtUserInfo<ID> jwtUserInfo;
        //从token中解析JWS对象
        JWSObject jwsObject = JWSObject.parse(token);
        //创建HMAC验证器
        JWSVerifier jwsVerifier = new MACVerifier(HuSecretUtils.encryptMd5(jwtProperties.getSecret()));
        if (!jwsObject.verify(jwsVerifier)) {
            throw new JwtInvalidException("token签名不合法");
        }
        jwtUserInfo = getJwtUserInfo(token);
        assert jwtUserInfo != null;
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new JwtExpireException("token已过期");
        }
        return jwtUserInfo;
    }

    private KeyPair getKeyPair() {
        KeyPair keyPair = getKeyPairMap().get("keyPair");
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
