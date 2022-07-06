package com.github.sparkzxl.alarm.feishutalk.sign;


import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.sign.SignResult;

/**
 * description: FeiShuTalk签名接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:56:35
 */
public class FeiShuTalkAlarmSignAlgorithm implements AlarmSignAlgorithm<SignResult> {

    @Override
    public SignResult sign(String secret) throws Exception {
        Long timestamp = System.currentTimeMillis();
        String sign = algorithm(timestamp, secret);
        return new SignResult(sign, timestamp);
    }

    @Override
    public String unionId() {
        return AlarmType.FEISHU.getType();
    }
}
