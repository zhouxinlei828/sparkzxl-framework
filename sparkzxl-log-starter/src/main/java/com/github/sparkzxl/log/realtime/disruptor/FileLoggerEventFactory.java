package com.github.sparkzxl.log.realtime.disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * description: 文件日志事件工厂类
 *
 * @author zhouxinlei
 * @date 2021-06-04 09:45:02
*/
public class FileLoggerEventFactory implements EventFactory<FileLoggerEvent> {
    @Override
    public FileLoggerEvent newInstance() {
        return new FileLoggerEvent();
    }
}
