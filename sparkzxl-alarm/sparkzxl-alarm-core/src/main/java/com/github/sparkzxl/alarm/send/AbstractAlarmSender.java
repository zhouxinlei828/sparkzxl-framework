package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.handler.AlarmExecutor;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.message.MarkDownMessage;
import com.github.sparkzxl.alarm.message.TextMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description:  AbstractAlarmSender
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:54:30
 */
public abstract class AbstractAlarmSender implements AlarmSender {

    protected TextMessage textMessage;
    protected MarkDownMessage markDownMessage;
    protected final AlarmProperties alarmProperties;
    protected final Map<String, AlarmExecutor> executorMap;

    public AbstractAlarmSender(AlarmProperties alarmProperties,
                               TextMessage textMessage,
                               MarkDownMessage markDownMessage,
                               List<AlarmExecutor> alarmExecutorList) {
        this.alarmProperties = alarmProperties;
        this.textMessage = textMessage;
        this.markDownMessage = markDownMessage;
        executorMap = new ConcurrentHashMap<>();
        if (!CollectionUtils.isEmpty(alarmExecutorList)) {
            for (AlarmExecutor alarmExecutor : alarmExecutorList) {
                executorMap.put(alarmExecutor.named(), alarmExecutor);
            }
        }
    }

    /**
     * 消息类型校验
     *
     * @param messageSubType 消息类型
     * @return 消息生成器
     */
    protected CustomMessage customMessage(MessageSubType messageSubType) {
        return messageSubType == MessageSubType.TEXT ? textMessage : markDownMessage;
    }

}
