package com.github.sparkzxl.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description: oss属性注入
 *
 * @author: zhouxinlei
 * @date: 2020-09-12 22:25:55
 */
@Data
@ConfigurationProperties(prefix = "sparkzxl.oss")
public class OSSProperties {

    private Aliyun aliyun;
    private Minio minio;

    /**
     * Minio OSS.
     */
    @Data
    public static class Minio {
        private boolean active;
        private String endpoint;
        private String accessKey;
        private String secretKey;
    }

    /**
     * Aliyun OSS.
     */
    @Data
    public static class Aliyun {
        private boolean active;
        private String domain;
        private String endpoint;
        private String accessKeyId;
        private String accessKeySecret;
    }

}
