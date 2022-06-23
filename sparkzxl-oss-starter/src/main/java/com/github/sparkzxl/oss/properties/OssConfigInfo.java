package com.github.sparkzxl.oss.properties;

import com.amazonaws.regions.Regions;
import lombok.Data;

import java.io.Serializable;

/**
 * description: oss属性配置信息
 *
 * @author zhouxinlei
 */
@Data
public class OssConfigInfo implements Serializable {


    private static final long serialVersionUID = -6310199657139774329L;

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
     * true path-style nginx 反向代理和S3默认支持 pathStyle false
     * supports virtual-hosted-style 阿里云等需要配置为 virtual-hosted-style模式
     */
    private Boolean pathStyleAccess = true;

    /**
     * 区域
     */
    private Regions region = Regions.CN_NORTH_1;

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

}
