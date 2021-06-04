package com.github.sparkzxl.log.realtime.disruptor;

import com.github.sparkzxl.log.entity.LoggerMessage;

/**
 * description: 进程日志事件内容载体
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:44:34
 */
public class LoggerEvent {

    private LoggerMessage log;

    public LoggerMessage getLog() {
        return log;
    }

    public void setLog(LoggerMessage log) {
        this.log = log;
    }
}
