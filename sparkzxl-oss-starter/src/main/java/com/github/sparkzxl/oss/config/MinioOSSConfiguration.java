package com.github.sparkzxl.oss.config;

import com.github.sparkzxl.oss.properties.OSSProperties;
import com.github.sparkzxl.oss.storage.MinioStorage;
import com.github.sparkzxl.oss.storage.Storage;
import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: Minio oss configuration
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:39:14
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "sparkzxl.oss.minio", name = "active", havingValue = "true")
@ConditionalOnClass(MinioClient.class)
class MinioOSSConfiguration {

    /**
     * Minio storage storage
     *
     * @param ossProperties the oss properties
     * @return
     * @throws InvalidPortException     the invalid port exception
     * @throws InvalidEndpointException the invalid endpoint exception
     */
    @Bean
    @Qualifier("minioStorage")
    Storage minioStorage(OSSProperties ossProperties) throws InvalidPortException, InvalidEndpointException {
        OSSProperties.Minio minio = ossProperties.getMinio();
        MinioClient minioClient = new MinioClient(minio.getEndpoint(),
                minio.getAccessKey(),
                minio.getSecretKey());
        return new MinioStorage(minioClient);
    }


}
