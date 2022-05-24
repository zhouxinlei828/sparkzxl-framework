package com.github.sparkzxl.alarm.executor;

import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.properties.AlarmProperties;

/**
 * description: 邮件告警执行器
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:29:20
 */
public class MailAlarmExecutor extends AbstractAlarmExecutor {

    @Override
    protected <T extends MsgType> AlarmResponse sendAlarm(String alarmId, AlarmProperties.AlarmConfig alarmConfig, T message) {
        return AlarmResponse.failed(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED);
    }

    @Override
    public String named() {
        return AlarmType.MAIL.getType();
    }
}
