package com.github.sparkzxl.signature.server.method;

import java.util.Map;

/**
 * description: 签名处理Processor
 *
 * @author zhouxinlei
 * @since 2024-07-08 14:35:59
 */
public interface SignProcessor {

    /**
     * 校验签名是否一致
     *
     * @param appKey    签名应用Key
     * @param timestamp 时间戳
     * @param nonce     随机数
     * @param sign      签名
     * @param params    待签名数据
     * @return 验签结果
     */
    boolean verifySign(String appKey, String timestamp, String nonce, String sign, Map<String, Object> params);

}
