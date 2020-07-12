package com.sparksys.commons.log.event;

import com.sparksys.commons.log.entity.OperateLog;
import org.springframework.context.ApplicationEvent;

/**
 * description: 系统日志事件
 *
 * @author: zhouxinlei
 * @date: 2020-07-12 00:01:24
 */
public class SysLogEvent extends ApplicationEvent {
    public SysLogEvent(OperateLog source) {
        super(source);
    }
}
