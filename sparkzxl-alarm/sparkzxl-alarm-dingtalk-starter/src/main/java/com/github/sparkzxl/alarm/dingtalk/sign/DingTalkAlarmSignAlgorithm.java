package com.github.sparkzxl.alarm.dingtalk.sign;


import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.sign.SignResult;

/**
 * description: DingTalk签名接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:56:35
 */
public class DingTalkAlarmSignAlgorithm implements AlarmSignAlgorithm<SignResult> {

    @Override
    public SignResult sign(String secret) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String sign = algorithm(timestamp, secret);
        return new SignResult(sign, timestamp);
    }

    @Override
    public String unionId() {
        return AlarmChannel.DINGTALK.getType();
    }
}
