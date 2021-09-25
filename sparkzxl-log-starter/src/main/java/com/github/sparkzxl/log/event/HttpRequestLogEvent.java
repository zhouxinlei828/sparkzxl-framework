package com.github.sparkzxl.log.event;

import com.github.sparkzxl.log.entity.RequestInfoLog;
import org.springframework.context.ApplicationEvent;

/**
 * description: 请求日志事件
 *
 * @author charles.zhou
 * @date 2021-05-24 12:17:23
 */
public class HttpRequestLogEvent extends ApplicationEvent {

    public HttpRequestLogEvent(RequestInfoLog source) {
        super(source);
    }

}
