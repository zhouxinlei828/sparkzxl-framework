package com.github.sparkzxl.alarm.feishutalk.sign;


import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.sign.SignResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * description: FeiShuTalk签名接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:56:35
 */
public class FeiShuTalkAlarmSignAlgorithm implements AlarmSignAlgorithm<SignResult> {

    @Override
    public SignResult sign(String secret) throws Exception {
        // 注意这里是秒级时间戳
        Long timestamp = System.currentTimeMillis() / 1000;
        String sign = algorithm(timestamp, secret);
        return new SignResult(sign, timestamp);
    }

    @Override
    public String algorithm(Long timestamp, String secret) throws Exception {
        // 把timestamp+"\n"+密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;
        // 使用HmacSHA256算法计算签名
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(new byte[]{});
        return new String(Base64.getEncoder().encode(signData));
    }

    @Override
    public String unionId() {
        return AlarmChannel.FEISHU.getType();
    }
}
