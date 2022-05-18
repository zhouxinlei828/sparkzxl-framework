package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.support.AlarmIdGenerator;

/**
 * description: 告警管理
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:26:49
 */
public class AlarmManagerBuilder {

    protected AlarmExceptionCallback alarmExceptionCallback;

    protected AlarmAsyncCallback alarmAsyncCallback;

    protected CustomMessage textMessage;

    protected CustomMessage markDownMessage;

    protected AlarmIdGenerator alarmIdGenerator;

    protected AlarmSignAlgorithm alarmSignAlgorithm;

    public AlarmExceptionCallback getAlarmExceptionCallback() {
        return alarmExceptionCallback;
    }

    public void setAlarmExceptionCallback(AlarmExceptionCallback alarmExceptionCallback) {
        this.alarmExceptionCallback = alarmExceptionCallback;
    }

    public AlarmAsyncCallback getAlarmAsyncCallback() {
        return alarmAsyncCallback;
    }

    public void setAlarmAsyncCallback(AlarmAsyncCallback alarmAsyncCallback) {
        this.alarmAsyncCallback = alarmAsyncCallback;
    }

    public CustomMessage getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(CustomMessage textMessage) {
        this.textMessage = textMessage;
    }

    public CustomMessage getMarkDownMessage() {
        return markDownMessage;
    }

    public void setMarkDownMessage(CustomMessage markDownMessage) {
        this.markDownMessage = markDownMessage;
    }

    public AlarmIdGenerator getAlarmIdGenerator() {
        return alarmIdGenerator;
    }

    public void setAlarmIdGenerator(AlarmIdGenerator alarmIdGenerator) {
        this.alarmIdGenerator = alarmIdGenerator;
    }

    public AlarmSignAlgorithm getAlarmSignAlgorithm() {
        return alarmSignAlgorithm;
    }

    public void setAlarmSignAlgorithm(AlarmSignAlgorithm alarmSignAlgorithm) {
        this.alarmSignAlgorithm = alarmSignAlgorithm;
    }
}
