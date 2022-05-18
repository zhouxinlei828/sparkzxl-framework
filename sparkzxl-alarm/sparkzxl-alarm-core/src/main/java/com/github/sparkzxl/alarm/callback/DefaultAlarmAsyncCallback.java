package com.github.sparkzxl.alarm.callback;

import lombok.extern.slf4j.Slf4j;

/**
 * description: 默认异步执行回调
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:34:47
 */
@Slf4j
public class DefaultAlarmAsyncCallback implements AlarmAsyncCallback {
    @Override
    public void execute(String alarmId, String result) {
        if (log.isDebugEnabled()) {
            log.debug("AlarmId=[{}], result=[{}].", alarmId, result);
        }
    }
}
