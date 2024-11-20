package com.github.sparkzxl.oss.properties;

import lombok.Data;

import java.io.Serializable;

/**
 * description: oss属性配置信息
 *
 * @author zhouxinlei
 */
@Data
public class Configuration implements Serializable {

    private static final long serialVersionUID = 3576218154929292921L;
    /**
     * Client Id
     */
    private String clientId;

    /**
     * Oss Client Type
     */
    private String clientType;

    /**
     * 对象存储服务的URL
     */
    private String endpoint;

    /**
     * 自定义域名
     */
    private String domain;

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

    /**
     * 上传文件限制白名单，逗号分割
     */
    private String fileFormat;

}
