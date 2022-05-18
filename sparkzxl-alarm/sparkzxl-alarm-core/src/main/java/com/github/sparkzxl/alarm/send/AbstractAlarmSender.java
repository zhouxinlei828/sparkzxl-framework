package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;

/**
 * description:  AbstractAlarmSender
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:54:30
 */
public abstract class AbstractAlarmSender implements AlarmSender {

    protected final AlarmProperties alarmProperties;
    protected final AlarmManagerBuilder alarmManagerBuilder;

    public AbstractAlarmSender(AlarmProperties alarmProperties, AlarmManagerBuilder alarmManagerBuilder) {
        this.alarmProperties = alarmProperties;
        this.alarmManagerBuilder = alarmManagerBuilder;
    }

    /**
     * 消息类型校验
     *
     * @param messageSubType 消息类型
     * @return 消息生成器
     */
    protected CustomMessage customMessage(MessageSubType messageSubType) {
        return messageSubType == MessageSubType.TEXT ? alarmManagerBuilder.getTextMessage() : alarmManagerBuilder.getMarkDownMessage();
    }

    /**
     * 异常回调
     *
     * @param alarmId   alarmId
     * @param message   message
     * @param exception ex
     * @param <T>       T
     */
    protected <T> void exceptionCallback(String alarmId, T message, AlarmException exception) {
        AlarmCallback alarmCallback = new AlarmCallback(alarmId, message, exception);
        alarmManagerBuilder.getAlarmExceptionCallback().execute(alarmCallback);
    }

}
