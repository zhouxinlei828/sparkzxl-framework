package com.github.sparkzxl.alarm.callback;

import com.github.sparkzxl.alarm.send.AlarmCallback;

/**
 * description: 通知异常回调
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:23:27
 */
public interface AlarmExceptionCallback {

    /**
     * 通知回调执行
     *
     * @param alarmCallback 异常回调信息
     */
    void execute(AlarmCallback alarmCallback);
}
