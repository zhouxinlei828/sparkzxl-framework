package com.github.sparkzxl.log.event;


import com.github.sparkzxl.core.context.AppContextHolder;
import com.github.sparkzxl.log.entity.OptLogRecordDetail;
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
 * @date 2021-05-24 12:19:34
 */
@Slf4j
@RequiredArgsConstructor
public class OptLogListener {

    private final Consumer<OptLogRecordDetail> consumer;

    @Async
    @EventListener(OptLogEvent.class)
    public void saveRequestLog(OptLogEvent event) {
        OptLogRecordDetail optLogRecordDetail = (OptLogRecordDetail) event.getSource();
        if (ObjectUtils.isEmpty(optLogRecordDetail)) {
            log.warn("忽略操作日志记录");
            return;
        }
        Optional.ofNullable(optLogRecordDetail.getTenantId()).ifPresent(AppContextHolder::setTenant);
        if (log.isDebugEnabled()) {
            log.debug("用户行为记录：租户：【{}】 请求接口：【{}】 操作人【{}】 业务类型：【{}】 业务日志：【{}】",
                    optLogRecordDetail.getTenantId(), optLogRecordDetail.getRequestUrl(), optLogRecordDetail.getOperator(),
                    optLogRecordDetail.getCategory(), optLogRecordDetail.getDetail());
        }
        consumer.accept(optLogRecordDetail);
    }

}
