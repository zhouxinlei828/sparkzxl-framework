package com.github.sparkzxl.log;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.github.sparkzxl.AlarmLogFactoryExecute;
import com.github.sparkzxl.AlarmLogContext;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.entity.AlarmLogInfo;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 日志告警Appender
 *
 * @author zhoux
 * @date 2021-08-21 13:19:52
 */
public class LogAlarmLogbackAsyncAppender extends AsyncAppender {

    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
            4,
            0,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(30),
            new DefaultThreadFactory("log-alarm"));

    @Override
    public void doAppend(ILoggingEvent eventObject) {
        if (eventObject instanceof LoggingEvent) {
            LoggingEvent loggingEvent = (LoggingEvent) eventObject;
            Level level = loggingEvent.getLevel();
            ThrowableProxy throwableProxy = (ThrowableProxy) loggingEvent.getThrowableProxy();
            String traceId = TraceContext.traceId();
            if (Objects.nonNull(throwableProxy)) {
                String applicationName = SpringContextUtils.getApplicationName();
                Throwable throwable = throwableProxy.getThrowable();
                if (AlarmLogContext.doWarnException(throwable)) {
                    StackTraceElement[] stackTraceElements = throwable.getStackTrace();
                    AlarmLogInfo alarmLogInfo = AlarmLogInfo.builder()
                            .applicationName(applicationName)
                            .message(loggingEvent.getFormattedMessage())
                            .throwableName(throwable.getClass().getName())
                            .threadName(loggingEvent.getThreadName())
                            .traceId(traceId)
                            .build();
                    if (ArrayUtils.isNotEmpty(stackTraceElements)) {
                        StackTraceElement stackTraceElement = stackTraceElements[0];
                        alarmLogInfo.setClassName(stackTraceElement.getClassName())
                                .setFileName(stackTraceElement.getFileName())
                                .setMethodName(stackTraceElement.getMethodName())
                                .setLineNumber(stackTraceElement.getLineNumber());
                    }
                    CompletableFuture.runAsync(() -> AlarmLogFactoryExecute.execute(alarmLogInfo
                            , throwable), threadPoolExecutor);
                }
            } else if (level.equals(Level.ERROR)) {
                String applicationName = SpringContextUtils.getApplicationName();
                AlarmLogInfo alarmLogInfo = AlarmLogInfo.builder()
                        .applicationName(applicationName)
                        .message(loggingEvent.getFormattedMessage())
                        .threadName(loggingEvent.getThreadName())
                        .traceId(traceId)
                        .build();
                CompletableFuture.runAsync(() -> AlarmLogFactoryExecute.execute(alarmLogInfo
                        , null), threadPoolExecutor);
            }
        }
    }
}
