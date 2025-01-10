package com.github.sparkzxl.signature.algorithm;

import com.github.sparkzxl.signature.entity.SignResult;
import com.github.sparkzxl.signature.utils.SM3SignUtil;

import java.util.Map;

/**
 * description: SHA256RSA签名算法
 *
 * @author zhouxinlei
 * @since 2024-05-20 15:12:05
 */
public class SM3SignAlgorithm implements SignAlgorithm {

    @Override
    public SignResult sign(Map<String, Object> paramMap, Long timestamp, String nonce, String secret) {
        String sign = SM3SignUtil.sign(paramMap, secret);
        return new SignResult(sign, timestamp, nonce);
    }

    @Override
    public boolean verify(Map<String, Object> paramMap, String secret, String sign) {
        return SM3SignUtil.verifySign(paramMap, secret, sign);
    }

    @Override
    public String getType() {
        return "SM3";
    }
}
