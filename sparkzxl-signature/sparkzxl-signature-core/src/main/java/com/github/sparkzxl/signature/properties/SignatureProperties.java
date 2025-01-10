package com.github.sparkzxl.signature.properties;

import com.github.sparkzxl.signature.constant.enums.AlgorithmEnum;
import com.github.sparkzxl.signature.constant.enums.SignTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;
import java.util.Map;

/**
 * description: 签名配置
 *
 * @author zhouxinlei
 * @since 2024-05-20 10:06:17
 */
@ConfigurationProperties(prefix = "phoenix.signature")
@Data
public class SignatureProperties implements Serializable {

    private static final long serialVersionUID = 5535285015830449402L;

    private Map<String, AppProperties> provider;

    /**
     * 签名类型
     */
    private boolean enableSSL;

    /**
     * description: ssl 配置
     *
     * @author zhouxinlei
     * @since 2024-05-20 10:13:53
     */
    @Data
    public static class AppProperties {

        /**
         * 是否启用该AKSK，1表示启用，0表示禁用。
         */
        private boolean enabled;

        /**
         * 访问密钥（AK），用于标识客户身份
         */
        private String appKey;

        /**
         * 秘密密钥（SK），用于生成签名和进行身份验证
         */
        private String appSecret;

        /**
         * AKSK有效期起始时间
         */
        private String validFrom;

        /**
         * AKSK有效期结束时间
         */
        private String validTo;

        /**
         * 逗号分隔的允许访问的接口/端点列表
         */
        private String allowedEndpoints;
        /**
         * 签名模式
         */
        private SignTypeEnum signType;
        /**
         * 签名算法,默认为HmacSHA256
         */
        private String algorithm = AlgorithmEnum.HmacSHA256.getValue();

    }

    /**
     * description: ssl 配置
     *
     * @author zhouxinlei
     * @since 2024-05-20 10:13:53
     */
    @Data
    public static class SSLContextProperties {

        private String protocol = "TLS";

        private String keyStorePath;

        private String keyStorePassword;

    }

}
