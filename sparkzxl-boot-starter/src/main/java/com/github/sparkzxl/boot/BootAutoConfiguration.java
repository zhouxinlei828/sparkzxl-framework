package com.github.sparkzxl.boot;

import com.github.sparkzxl.boot.undertow.UndertowServerFactoryCustomizer;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * description: boot自动装配
 *
 * @author zhouxinlei
 * @since 2021-07-25 18:10:07
 */
@EnableSpringUtil
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@Configuration
public class BootAutoConfiguration {

    @Bean
    public ApplicationLogRunner applicationRunner(ApplicationContext applicationContext) {
        return new ApplicationLogRunner(applicationContext);
    }

    @Bean
    @ConditionalOnClass(Undertow.class)
    public UndertowServerFactoryCustomizer getUndertowServerFactoryCustomizer() {
        return new UndertowServerFactoryCustomizer();
    }

}
