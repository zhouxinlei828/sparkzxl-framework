package com.github.sparkzxl.distributed.cloud.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;

/**
 * description: Application启动后运行
 * 日志打印微服务关键配置信息(服务名、接口文档、访问地址)
 *
 * @author zhouxinlei
 */
@Slf4j
public class CloudApplicationInitRunner implements ApplicationRunner, Ordered {

    public CloudApplicationInitRunner() {
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("\nspring cloud is running!");
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
    }
}
