package com.github.sparkzxl.signature.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * description: 签名返回体
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:54:18
 */
@Setter
@Getter
public class SignResult extends BaseSign {

    /**
     * 签名
     */
    private String sign;
    /**
     * 时间戳
     */
    private Long timestamp;
    /**
     * 随机数
     */
    private String nonce;

    public SignResult(String sign, Long timestamp, String nonce) {
        this.sign = sign;
        this.timestamp = timestamp;
        this.nonce = nonce;
    }

    @Override
    public String transfer() {
        return SEPERATOR + "sign=" + this.sign +
                SEPERATOR + "timestamp=" + this.timestamp +
                SEPERATOR + "nonce=" + this.nonce;
    }
}
