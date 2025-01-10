package com.github.sparkzxl.signature.algorithm;

import com.github.sparkzxl.signature.entity.SignResult;

import java.util.Map;


/**
 * description: 签名接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:52:38
 */
public interface SignAlgorithm {

    /**
     * 加签
     *
     * @param paramMap      数据
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param secret    密钥
     * @return SignResult
     */
    SignResult sign(Map<String, Object> paramMap, Long timestamp, String nonce, String secret);

    /**
     * 验签
     *
     * @param paramMap 数据
     * @param secret   密钥
     * @param sign     签名
     * @return boolean
     */
    boolean verify(Map<String, Object> paramMap, String secret, String sign);

    /**
     * 签名算法
     *
     * @return String
     */
    String getType();

}
