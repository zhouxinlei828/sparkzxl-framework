package com.github.sparkzxl.alarm.callback;

/**
 * description: 异步执行回调接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:33:23
 */
public interface AlarmAsyncCallback {

    /**
     * 异步执行回调函数
     *
     * @param alarmId 告警id
     * @param result  返回结果
     */
    void execute(String alarmId, String result);
}