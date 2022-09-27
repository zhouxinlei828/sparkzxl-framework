package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.annotation.OSSUpload;
import com.github.sparkzxl.oss.aop.OSSUploadAnnotationAdvisor;
import com.github.sparkzxl.oss.aop.OSSUploadInterceptor;
import com.github.sparkzxl.oss.listener.DefaultUploadListener;
import com.github.sparkzxl.oss.listener.UploadListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * description: oss aop自动配置
 *
 * @author zhouxinlei
 */
@ConditionalOnProperty(name = "oss.enabled", havingValue = "true")
public class OssAspectAutoConfiguration {

    @Bean
    public OSSUploadInterceptor uploadInterceptor() {
        return new OSSUploadInterceptor();
    }

    @Bean
    public OSSUploadAnnotationAdvisor uploadAnnotationAdvisor(OSSUploadInterceptor uploadInterceptor) {
        return new OSSUploadAnnotationAdvisor(uploadInterceptor, OSSUpload.class, Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public UploadListener defaultUploadListener() {
        return new DefaultUploadListener();
    }
}
