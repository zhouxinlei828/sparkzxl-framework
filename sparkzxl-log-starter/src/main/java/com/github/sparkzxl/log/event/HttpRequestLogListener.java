package com.github.sparkzxl.log.event;


import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.log.entity.RequestInfoLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import java.util.Optional;
import java.util.function.Consumer;


/***
 * description: 异步监听请求日志
 *
 * @author charles.zhou
 */
@Slf4j
@RequiredArgsConstructor
public class HttpRequestLogListener {

    private final Consumer<RequestInfoLog> consumer;

    @Async
    @EventListener(HttpRequestLogEvent.class)
    public void saveRequestLog(HttpRequestLogEvent event) {
        RequestInfoLog requestInfoLog = (RequestInfoLog) event.getSource();
        if (ObjectUtils.isEmpty(requestInfoLog)) {
            log.warn("忽略请求日志记录");
            return;
        }
        Optional.ofNullable(requestInfoLog.getTenantId()).ifPresent(RequestLocalContextHolder::setTenant);
        if (log.isDebugEnabled()) {
            log.debug("租户:【{}】 用户名:【{}】 请求接口:【{}】 请求耗时:【{}】",
                    requestInfoLog.getTenantId(), requestInfoLog.getUserName(),
                    requestInfoLog.getRequestUrl(), requestInfoLog.getConsumingTime());
        }
        consumer.accept(requestInfoLog);
    }

}
