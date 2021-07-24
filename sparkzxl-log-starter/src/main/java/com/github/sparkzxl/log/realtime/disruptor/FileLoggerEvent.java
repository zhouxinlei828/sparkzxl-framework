package com.github.sparkzxl.log.realtime.disruptor;

/**
 * description: 文件日志事件内容载体
 *
 * @author zhouxinlei
 */
public class FileLoggerEvent {
    private String log;

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}