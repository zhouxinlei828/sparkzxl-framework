package com.github.sparkzxl.alarm.callback;

import com.github.sparkzxl.alarm.send.AlarmCallback;
import com.github.sparkzxl.alarm.exception.AlarmException;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 默认异常通知处理
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:25:03
 */
@Slf4j
public class DefaultAlarmExceptionCallback implements AlarmExceptionCallback {

    @Override
    public void execute(AlarmCallback alarmCallback) {
        AlarmException exception = alarmCallback.getException();
        log.error("异常静默处理:{}-{}.",
                exception.getErrorCode(),
                exception.getErrorMsg()
        );
    }
}
