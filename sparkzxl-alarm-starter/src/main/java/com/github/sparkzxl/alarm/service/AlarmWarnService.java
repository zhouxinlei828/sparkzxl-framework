package com.github.sparkzxl.alarm.service;

import com.github.sparkzxl.alarm.entity.NotifyMessage;

/**
 * description: 日志告警服务类
 *
 * @author zhoux
 */
public interface AlarmWarnService {

    /**
     * 发送信息
     *
     * @param notifyMessage message
     */
    void send(NotifyMessage notifyMessage);

}
