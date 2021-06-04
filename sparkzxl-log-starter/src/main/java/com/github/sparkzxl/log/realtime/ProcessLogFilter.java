package com.github.sparkzxl.log.realtime;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;
import cn.hutool.core.date.DatePattern;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.log.entity.LoggerMessage;

import java.sql.Date;

/**
 * description: 实时日志过滤器
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:33
 */
public class ProcessLogFilter extends Filter<ILoggingEvent> {

    @Override
    public FilterReply decide(ILoggingEvent event) {
        String levelStr = event.getLevel().levelStr;
        if (event.getLevel().equals(Level.INFO)) {
            levelStr = levelStr.replace("INFO", "<span style='color: green;'>INFO</span>");
        } else if (event.getLevel().equals(Level.DEBUG)) {
            levelStr = levelStr.replace("DEBUG", "<span style='color: blue;'>DEBUG</span>");
        } else if (event.getLevel().equals(Level.WARN)) {
            levelStr = levelStr.replace("WARN", "<span style='color: orange;'>WARN</span>");
        } else if (event.getLevel().equals(Level.ERROR)) {
            levelStr = levelStr.replace("ERROR", "<span style='color: red;'>ERROR</span>");
        }
        String loggerName = event.getLoggerName();
        loggerName = "<span style='color: #99BB7D;'>" + loggerName + "</span>";
        String applicationName = "<span style='color: #73B5EE;'>application</span>: ".concat(SpringContextUtils.getApplicationName());
        LoggerMessage loggerMessage = new LoggerMessage(
                DateUtils.format(new Date(event.getTimeStamp()), DatePattern.NORM_DATETIME_MS_PATTERN),
                applicationName,
                levelStr,
                event.getFormattedMessage(),
                loggerName,
                event.getThreadName()
        );
        LoggerDisruptorQueue.publishEvent(loggerMessage);
        return FilterReply.ACCEPT;
    }
}
