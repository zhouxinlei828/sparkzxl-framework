package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.exception.AlarmException;

/**
 * 异常回调信息实体
 *
 * @author Jaemon
 * @since 1.0
 */
public class AlarmCallback<T> {

    /**
     * 处理唯一id
     */
    private String alarmId;
    /**
     * 通知信息
     */
    private T message;
    /**
     * 异常对象
     */
    private AlarmException exception;

    public AlarmCallback() {
    }

    public AlarmCallback(String alarmId, T message, AlarmException e) {
        this.alarmId = alarmId;
        this.message = message;
        this.exception = e;
    }

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    public AlarmException getException() {
        return exception;
    }

    public void setException(AlarmException exception) {
        this.exception = exception;
    }
}

