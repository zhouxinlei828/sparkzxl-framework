package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.message.MarkDownMessageTemplate;
import com.github.sparkzxl.alarm.message.MessageTemplate;
import com.github.sparkzxl.alarm.message.TextMessageTemplate;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.CollectionUtils;

/**
 * description:  AbstractAlarmSender
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:54:30
 */
public abstract class AbstractAlarmClient implements AlarmClient {

    protected TextMessageTemplate textMessage;
    protected MarkDownMessageTemplate markDownMessage;
    protected final AlarmProperties alarmProperties;
    protected final Map<String, AlarmExecutor> executorMap;

    public AbstractAlarmClient(AlarmProperties alarmProperties,
            TextMessageTemplate textMessage,
            MarkDownMessageTemplate markDownMessage,
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
     * 消息模板转换
     *
     * @param messageSubType 消息类型
     */
    protected void convertMessage(MessageSubType messageSubType, AlarmRequest request) {
        MessageTemplate messageTemplate = messageSubType == MessageSubType.TEXT ? textMessage : markDownMessage;
        String message = messageTemplate.message(request);
        request.setContent(message);
    }

}
