package com.github.sparkzxl.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * description: tracer 监听
 *
 * @author zhouxinlei
 * @since 2022-07-25 14:22:11
 */
public class TracerContextListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(TracerContextListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // 异步调用下，第一次启动在某些情况下可能存在丢失上下文的问题
        logger.info("Initialize Strategy Tracer Context after Application started...");
        TracerContext.getCurrentContext();
    }
}
