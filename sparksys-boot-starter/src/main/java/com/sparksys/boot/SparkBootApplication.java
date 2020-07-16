package com.sparksys.boot;

import com.sparksys.boot.application.event.ApplicationRunner;
import com.sparksys.core.utils.SpringContextUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * description: sparksys 启动类
 *
 * @author: zhouxinlei
 * @date: 2020-07-15 21:49:59
*/
@Import(SpringContextUtils.class)
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class SparkBootApplication {

    @Bean
    public ApplicationRunner applicationRunner(ApplicationContext applicationContext){
        return new ApplicationRunner(applicationContext);
    }

}
