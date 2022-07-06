package com.github.sparkzxl.alarm.wetalk.entity;

import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;

/**
 * description: WeTalk请求体
 *
 * @author zhouxinlei
 * @since 2022-05-18 17:13:18
 */
public class WeTalkMessage extends MsgType {
    public WeTalkMessage() {
        setAlarmType(AlarmType.WETALK);
    }
}