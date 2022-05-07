package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.OssConfigInfo;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description:  A config provider of {@link OssConfigInfo} oss
 *
 * @author zhouxinlei
 * @since 2022-05-05 11:26:16
 */
public interface OssConfigProvider {

    /**
     * 加载客户端配置信息
     *
     * @param clientType 客户端类型
     * @return List<OssConfigInfo>
     */
    List<OssConfigInfo> loadOssConfigInfo(String clientType);


    /**
     * 校验配置属性是否合法
     *
     * @param configInfo 配置信息
     */
    default void validateOssConfigInfo(OssConfigInfo configInfo) {
        if (StringUtils.isEmpty(configInfo.getClientId())) {
            throw new RuntimeException("Oss client id cannot be empty.");
        }
        if (StringUtils.isEmpty(configInfo.getClientType())) {
            throw new RuntimeException("Oss client clientType cannot be empty.");
        }
        if (StringUtils.isEmpty(configInfo.getEndpoint())) {
            throw new RuntimeException("Oss client endpoint cannot be empty.");
        }
        if (StringUtils.isEmpty(configInfo.getAccessKey())) {
            throw new RuntimeException("Oss client accessKey cannot be empty.");
        }
        if (StringUtils.isEmpty(configInfo.getSecretKey())) {
            throw new RuntimeException("Oss client secretKey cannot be empty.");
        }
    }
}
