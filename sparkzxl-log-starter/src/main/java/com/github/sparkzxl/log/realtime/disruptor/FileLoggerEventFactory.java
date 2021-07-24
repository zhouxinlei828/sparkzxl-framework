package com.github.sparkzxl.log.realtime.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * description: 文件日志事件工厂类
 *
 * @author zhouxinlei
 */
public class FileLoggerEventFactory implements EventFactory<FileLoggerEvent> {
    @Override
    public FileLoggerEvent newInstance() {
        return new FileLoggerEvent();
    }
}
