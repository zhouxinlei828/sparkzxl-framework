package com.github.sparkzxl.oss.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.github.sparkzxl.oss.properties.OSSProperties;
import com.github.sparkzxl.oss.storage.AliyunStorage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: Aliyun oss configuration
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:39:01
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "sparkzxl.oss.aliyun", name = "active", havingValue = "true")
@ConditionalOnClass(OSSClient.class)
public class AliyunOSSConfiguration {


    /**
     * Aliyun storage storage.
     *
     * @param ossProperties the oss properties
     * @return the storage
     */
    @Bean
    @Qualifier("aliyunStorage")
    AliyunStorage aliyunStorage(OSSProperties ossProperties) {
        OSSProperties.Aliyun aliyun = ossProperties.getAliyun();
        OSS ossClient = new OSSClientBuilder().build(aliyun.getEndpoint(),
                aliyun.getAccessKeyId(),
                aliyun.getAccessKeySecret());
        return new AliyunStorage(ossClient, aliyun.getEndpoint());
    }


}
