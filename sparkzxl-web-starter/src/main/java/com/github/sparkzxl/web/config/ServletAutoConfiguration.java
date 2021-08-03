package com.github.sparkzxl.web.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.CustomDispatcherServlet;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * description: Servlet自动装配注入
 *
 * @author zhouxinlei
 * @date 2021-08-03 12:06:41
 */
@Configuration
public class ServletAutoConfiguration {

    @Bean
    public ServletRegistrationBean<Servlet> dispatcherRegistration() {
        //注解扫描上下文
        AnnotationConfigWebApplicationContext applicationContext
                = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CustomDispatcherServlet.class);
        //通过构造函数指定dispatcherServlet的上下文
        DispatcherServlet dispatcherServlet
                = new CustomDispatcherServlet(applicationContext);
        //用ServletRegistrationBean包装servlet
        ServletRegistrationBean<Servlet> registrationBean
                = new ServletRegistrationBean<>(dispatcherServlet);
        registrationBean.setLoadOnStartup(1);
        return registrationBean;
    }

}
