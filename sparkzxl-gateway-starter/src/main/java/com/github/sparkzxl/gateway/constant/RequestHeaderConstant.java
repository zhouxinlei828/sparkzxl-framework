package com.github.sparkzxl.gateway.constant;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-15 19:57:19
 */
public interface RequestHeaderConstant {

    String X_CA_SIGNATURE = "signature";

    String X_CA_TIMESTAMP = "timestamp";

    String X_CA_NONCE = "nonce";

    /**
     * 签名方式 MD5  或 HMAC-SHA256
     */
    String X_CA_SIGN_TYPE = "Sign-Type";


}
