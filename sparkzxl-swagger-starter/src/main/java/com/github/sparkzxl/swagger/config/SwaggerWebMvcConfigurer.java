package com.github.sparkzxl.swagger.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * description: Swagger 配置
 *
 * @author charles.zhou
 * @date 2021-05-12 16:10:33
 */
public class SwaggerWebMvcConfigurer implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars" +
                "*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
