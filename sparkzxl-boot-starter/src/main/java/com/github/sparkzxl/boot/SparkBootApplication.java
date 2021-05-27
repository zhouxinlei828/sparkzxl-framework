package com.github.sparkzxl.boot;

import com.github.sparkzxl.boot.application.event.ApplicationInitRunner;
import com.github.sparkzxl.boot.infrastructure.annonation.EnableSpringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * description: sparkzxl boot 启动类
 *
 * @author zhouxinlei
 */
@EnableSpringUtil
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SparkBootApplication {

    @Bean
    public ApplicationInitRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationInitRunner(applicationContext);
    }

}
