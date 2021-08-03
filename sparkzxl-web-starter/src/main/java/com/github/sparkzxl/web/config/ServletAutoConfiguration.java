package com.github.sparkzxl.web.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.CustomDispatcherServlet;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;

/**
 * description: Servlet自动装配注入
 *
 * @author zhouxinlei
 * @date 2021-08-03 12:06:41
 */
@Configuration
public class ServletAutoConfiguration {

    public static final String DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME = "dispatcherServletRegistration";

    @Bean(name = DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME)
    public DispatcherServletRegistrationBean dispatcherServletRegistration(ObjectProvider<MultipartConfigElement> multipartConfig) {
        //注解扫描上下文
        AnnotationConfigWebApplicationContext applicationContext
                = new AnnotationConfigWebApplicationContext();
        applicationContext.register(CustomDispatcherServlet.class);
        //通过构造函数指定dispatcherServlet的上下文
        DispatcherServlet dispatcherServlet
                = new CustomDispatcherServlet(applicationContext);
        //用ServletRegistrationBean包装servlet
        DispatcherServletRegistrationBean registrationBean
                = new DispatcherServletRegistrationBean(dispatcherServlet, "/");
        registrationBean.setLoadOnStartup(1);
        multipartConfig.ifAvailable(registrationBean::setMultipartConfig);
        return registrationBean;
    }

}
