package com.github.sparkzxl.alarm.sign;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * description: DingTalk签名接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:52:38
 */
public interface AlarmSignAlgorithm<T extends BaseSign> {

    /**
     * 签名
     *
     * @param secret 密钥
     * @return T
     * @throws Exception 异常
     */
    T sign(String secret) throws Exception;

    default String algorithm(Long timestamp, String secret) throws Exception {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(
                stringToSign.getBytes(StandardCharsets.UTF_8)
        );
        return URLEncoder.encode(
                Base64.getEncoder().encodeToString(signData),
                StandardCharsets.UTF_8.name());
    }
}
