package com.github.sparkzxl.alarm.support;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.alarm.constant.AlarmConstant;

/**
 * description: AlarmId生成默认算法
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:20:15
 */
public class DefaultAlarmIdGenerator implements AlarmIdGenerator {

    @Override
    public String nextAlarmId() {
        StringBuilder alarmId = new StringBuilder(AlarmConstant.ALARM_PREFIX);
        String uuid = IdUtil.fastSimpleUUID();
        alarmId.append(uuid);
        return alarmId.toString();
    }

}
