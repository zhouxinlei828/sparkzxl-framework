package com.github.sparkzxl.oss.provider;


import com.github.sparkzxl.oss.properties.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * description: 抽象oss配置提供者
 *
 * @author zhouxinlei
 * @since 2022-05-07 16:54:45
 */
public abstract class AbstractOssConfigProvider implements OssConfigProvider {

    @Override
    public List<Configuration> loadConfigurationList() {
        List<Configuration> configList = list();
        for (Configuration configInfo : configList) {
            validateConfiguration(configInfo);
        }
        return configList;
    }

    /**
     * 校验配置属性是否合法
     *
     * @param configInfo 配置信息
     */
    public void validateConfiguration(Configuration configInfo) {
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

    /**
     * 获取配置信息列表
     *
     * @return List<OssConfigInfo>
     */
    protected abstract List<Configuration> list();

}
