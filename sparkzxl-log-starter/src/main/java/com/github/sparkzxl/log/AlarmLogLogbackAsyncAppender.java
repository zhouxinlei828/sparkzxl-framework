package com.github.sparkzxl.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.log.entity.AlarmLogInfo;
import com.github.sparkzxl.log.queue.AlarmTaskInfo;
import com.github.sparkzxl.log.queue.AlarmTaskQueue;
import com.github.sparkzxl.log.utils.ThrowableUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.MDC;

import java.util.Objects;

/**
 * description: 日志告警Appender
 *
 * @author zhoux
 */
@Getter
@Setter
public class AlarmLogLogbackAsyncAppender extends AsyncAppender {

    private String robotId;

    @Override
    public void doAppend(ILoggingEvent eventObject) {
        if (eventObject instanceof LoggingEvent) {
            LoggingEvent loggingEvent = (LoggingEvent) eventObject;
            Level level = loggingEvent.getLevel();
            ThrowableProxy throwableProxy = (ThrowableProxy) loggingEvent.getThrowableProxy();
            if (Objects.nonNull(throwableProxy)) {
                AlarmRequest alarmRequest = new AlarmRequest();
                alarmRequest.setTitle("服务系统异常告警");
                String traceId = MDC.get(BaseContextConstants.LOG_TRACE_ID);
                String applicationName = SpringContextUtils.getApplicationName();
                String environment = SpringContextUtils.getEnvironment();
                Throwable throwable = throwableProxy.getThrowable();
                if (AlarmLogContext.doWarnException(throwable)) {
                    StackTraceElement[] stackTraceElements = throwable.getStackTrace();
                    AlarmLogInfo alarmLogInfo = AlarmLogInfo.builder()
                            .applicationName(applicationName)
                            .environment(environment)
                            .message(loggingEvent.getFormattedMessage())
                            .throwableName(throwable.getClass().getName())
                            .threadName(loggingEvent.getThreadName())
                            .traceId(traceId).build();
                    if (ArrayUtils.isNotEmpty(stackTraceElements)) {
                        StackTraceElement stackTraceElement = stackTraceElements[0];
                        alarmLogInfo.setClassName(stackTraceElement.getClassName()).setFileName(stackTraceElement.getFileName())
                                .setMethodName(stackTraceElement.getMethodName()).setLineNumber(stackTraceElement.getLineNumber());
                    }
                    String message = ThrowableUtils.dingTalkMarkdownContent(alarmLogInfo, throwable);
                    sendLogAlarm(alarmRequest, message);
                }
            } else if (level.equals(Level.ERROR)) {
                AlarmRequest alarmRequest = new AlarmRequest();
                alarmRequest.setTitle("服务系统异常告警");
                String traceId = MDC.get(BaseContextConstants.LOG_TRACE_ID);
                String applicationName = SpringContextUtils.getApplicationName();
                String environment = SpringContextUtils.getEnvironment();
                AlarmLogInfo alarmLogInfo = AlarmLogInfo.builder()
                        .applicationName(applicationName)
                        .environment(environment)
                        .message(loggingEvent.getFormattedMessage())
                        .threadName(loggingEvent.getThreadName())
                        .traceId(traceId)
                        .build();
                String message = ThrowableUtils.dingTalkMarkdownContent(alarmLogInfo, null);
                sendLogAlarm(alarmRequest, message);
            }
        }
    }

    private void sendLogAlarm(AlarmRequest alarmRequest, String message) {
        alarmRequest.setContent(message);
        AlarmTaskQueue.getQueue().produce(new AlarmTaskInfo(robotId, alarmRequest));
    }
}
