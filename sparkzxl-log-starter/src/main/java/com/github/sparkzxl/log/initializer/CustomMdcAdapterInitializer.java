package com.github.sparkzxl.log.initializer;

import org.slf4j.TransmittableThreadLocalMdcAdapter;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.NonNull;

/**
 * description: 初始化TtlMDCAdapter实例，并替换MDC中的adapter对象
 *
 * @author zhouxinlei
 */
public class CustomMdcAdapterInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(@NonNull ConfigurableApplicationContext applicationContext) {
        //加载TtlMDCAdapter实例
        TransmittableThreadLocalMdcAdapter.getInstance();
    }
}
