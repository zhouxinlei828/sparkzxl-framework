package com.github.sparkzxl.signature.executor;

import com.github.sparkzxl.signature.entity.SignResult;

/**
 * description: 签名执行器
 *
 * @author zhouxinlei
 * @since 2024-05-20 10:23:10
 */
public interface SignatureExecutor<T> {

    /**
     * 加签
     *
     * @param appKey 签名应用Key标识
     * @param data      数据
     * @return SignResult
     */
    SignResult sign(String appKey, T data);

    /**
     * 验证签名
     *
     * @param appKey 签名应用Key标识
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param sign      签名数据
     * @param data      数据
     * @return boolean
     */
    boolean verify(String appKey, Long timestamp, String nonce, String sign, T data);

    /**
     * 类型
     *
     * @return String
     */
    String getType();
}
