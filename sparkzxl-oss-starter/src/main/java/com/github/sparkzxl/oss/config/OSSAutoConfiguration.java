package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.properties.OSSProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * description: oss自动配置
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:40:38
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(OSSProperties.class)
@Import({MinioOSSConfiguration.class, AliyunOSSConfiguration.class})
public class OSSAutoConfiguration {

}
