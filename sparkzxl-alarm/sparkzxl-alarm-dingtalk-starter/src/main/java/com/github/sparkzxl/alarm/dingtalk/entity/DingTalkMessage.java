package com.github.sparkzxl.alarm.dingtalk.entity;

import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;

/**
 * description: DingTalk请求体
 *
 * @author zhouxinlei
 * @since 2022-05-18 13:48:06
 */
public class DingTalkMessage extends MsgType {

    public DingTalkMessage() {
        setAlarmType(AlarmType.DINGTALK);
    }

}