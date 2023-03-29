package com.github.sparkzxl.jwt.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.support.JwtInvalidException;
import com.github.sparkzxl.core.util.DateUtils;
import com.github.sparkzxl.core.util.SecretUtil;
import com.github.sparkzxl.core.util.TimeUtil;
import com.github.sparkzxl.jwt.entity.JwtUserInfo;
import com.github.sparkzxl.jwt.properties.JwtProperties;
import com.github.sparkzxl.jwt.properties.KeyStoreProperties;
import com.github.sparkzxl.jwt.service.JwtTokenService;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.RSAKey;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * description: jwtToken 服务实现类
 *
 * @author zhouxinlei
 */
@Slf4j
public class JwtTokenServiceImpl implements JwtTokenService {

    private final JwtProperties jwtProperties;
    private final KeyStoreProperties KeyStoreProperties;
    private final Map<String, KeyPair> keyPairMap;

    public JwtTokenServiceImpl(JwtProperties jwtProperties, KeyStoreProperties keyStoreProperties, Map<String, KeyPair> keyPairMap) {
        this.jwtProperties = jwtProperties;
        this.KeyStoreProperties = keyStoreProperties;
        this.keyPairMap = keyPairMap;
    }

    @Override
    public String createTokenByRsa(JwtUserInfo jwtUserInfo) {
        JWSObject jwsObject = null;
        try {
            //创建JWS头，设置签名算法和类型
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();
            long seconds = TimeUtil.toSeconds(jwtProperties.getExpire(), jwtProperties.getUnit());
            Date expire = DateUtil.offsetSecond(new Date(), (int) seconds);
            jwtUserInfo.setExpire(expire);
            jwtUserInfo.setJti(UUID.randomUUID().toString());
            //将负载信息封装到Payload中
            String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
            Payload payload = new Payload(payloadStr);
            //创建JWS对象
            jwsObject = new JWSObject(jwsHeader, payload);
            //创建RSA签名器
            JWSSigner jwsSigner = new RSASSASigner(getRsaKey());
            //签名
            jwsObject.sign(jwsSigner);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionAssert.failure("生成token发生异常：".concat(e.getMessage()));
        }
        return jwsObject.serialize();
    }

    @Override
    public JwtUserInfo verifyTokenByRsa(String token) throws Exception {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
        assert jwtUserInfo != null;
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new JwtExpireException("token已过期");
        }
        return jwtUserInfo;
    }

    @Override
    public JwtUserInfo getJwtUserInfo(String token) throws Exception {
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        return JsonUtils.getJson().toJavaObject(payload, JwtUserInfo.class);
    }

    @Override
    public JwtUserInfo getAuthJwtInfo(String token) throws Exception {
        JwtUserInfo jwtUserInfo = new JwtUserInfo();
        JWSObject jwsObject = JWSObject.parse(token);
        String payload = jwsObject.getPayload().toString();
        Map<String, Object> objectMap = JsonUtils.getJson().toMap(payload);
        jwtUserInfo.setId((String) objectMap.getOrDefault("id", null));
        String username = (String) objectMap.getOrDefault("user_name", null);
        jwtUserInfo.setUsername(username);
        String name = (String) objectMap.getOrDefault("name", null);
        jwtUserInfo.setName(name);
        String clientId = (String) objectMap.getOrDefault("client_id", null);
        jwtUserInfo.setClientId(clientId);
        String sub = (String) objectMap.getOrDefault("sub", null);
        jwtUserInfo.setSub(sub);
        Long iat = (Long) objectMap.getOrDefault("iat", null);
        jwtUserInfo.setIat(iat);
        Long exp = (Long) objectMap.getOrDefault("exp", null);
        if (ObjectUtils.isNotEmpty(exp)) {
            jwtUserInfo.setExpire(DateUtils.date(exp * 1000));
        }
        String jti = (String) objectMap.getOrDefault("jti", null);
        jwtUserInfo.setJti(jti);
        String tenantId = (String) objectMap.getOrDefault("tenantId", null);
        jwtUserInfo.setTenantId(tenantId);
        List authorities = (List) objectMap.getOrDefault("authorities", null);
        jwtUserInfo.setAuthorities(authorities);
        return jwtUserInfo;
    }

    @Override
    public String createTokenByHmac(JwtUserInfo jwtUserInfo) {
        JWSObject jwsObject = null;
        try {
            //创建JWS头，设置签名算法和类型
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).
                    type(JOSEObjectType.JWT)
                    .build();
            //将负载信息封装到Payload中
            String payloadStr = JSONUtil.toJsonStr(jwtUserInfo);
            Payload payload = new Payload(payloadStr);
            //创建JWS对象
            jwsObject = new JWSObject(jwsHeader, payload);
            JWSSigner jwsSigner = new MACSigner(SecretUtil.encryptMd5(jwtProperties.getSecret()));
            jwsObject.sign(jwsSigner);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionAssert.failure("生成token发生异常：".concat(e.getMessage()));
        }
        return jwsObject.serialize();
    }

    @Override
    public JwtUserInfo verifyTokenByHmac(String token) throws Exception {
        JwtUserInfo jwtUserInfo;
        //从token中解析JWS对象
        JWSObject jwsObject = JWSObject.parse(token);
        //创建HMAC验证器
        JWSVerifier jwsVerifier = new MACVerifier(SecretUtil.encryptMd5(jwtProperties.getSecret()));
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
        KeyPair keyPair = this.keyPairMap.get("keyPair");
        if (ObjectUtils.isNotEmpty(keyPair)) {
            return keyPair;
        }
        keyPair = SecretUtil.keyPair(KeyStoreProperties.getPath(), "jwt", KeyStoreProperties.getPassword());
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
