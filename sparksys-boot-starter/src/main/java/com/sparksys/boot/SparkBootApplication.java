package com.sparksys.boot;

import com.sparksys.boot.application.event.ApplicationInitRunner;
import com.sparksys.boot.infrastructure.annonation.EnableSpringUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * description: sparksys 启动类
 *
 * @author: zhouxinlei
 * @date: 2020-07-15 21:49:59
 */
@EnableSpringUtil
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SparkBootApplication {

    @Bean
    public ApplicationInitRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationInitRunner(applicationContext);
    }

}
