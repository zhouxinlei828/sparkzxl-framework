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
public class OssProperties {

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String customDomain;

    /**
     * true path-style nginx 反向代理和S3默认支持 pathStyle {http://endpoint/bucketname} false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style
     * 模式{http://bucketname.endpoint}
     */
    private Boolean pathStyleAccess = true;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 区域
     */
    private String region;

    /**
     * Access key就像用户ID，可以唯一标识你的账户
     */
    private String accessKey;

    /**
     * Secret key是你账户的密码
     */
    private String secretKey;

    /**
     * 默认的存储桶名称
     */
    private String bucketName = "sparkzxl";


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
