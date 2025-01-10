package com.github.sparkzxl.signature.algorithm;

import com.github.sparkzxl.signature.constant.enums.AlgorithmEnum;
import com.github.sparkzxl.signature.entity.SignResult;
import com.github.sparkzxl.signature.utils.SignatureUtils;

import java.util.Map;

/**
 * description: HmacSHA256签名算法
 *
 * @author zhouxinlei
 * @since 2024-05-20 15:12:05
 */
public class HmacSHA256SignAlgorithm implements SignAlgorithm {

    @Override
    public SignResult sign(Map<String, Object> paramMap, Long timestamp, String nonce, String secret) {
        String sign = SignatureUtils.generateSignHmacSHA256(paramMap, secret);
        return new SignResult(sign, timestamp, nonce);
    }

    @Override
    public boolean verify(Map<String, Object> paramMap, String secret, String sign) {
        return SignatureUtils.verifySignHmacSHA256(paramMap, secret, sign);
    }

    @Override
    public String getType() {
        return AlgorithmEnum.HmacSHA256.getValue();
    }
}
