package com.github.sparkzxl.oss.provider;


import com.github.sparkzxl.oss.properties.OssConfigInfo;

import java.util.List;

/**
 * description: 抽象oss配置提供者
 *
 * @author zhouxinlei
 * @since 2022-05-07 16:54:45
 */
public abstract class AbstractOssConfigProvider implements OssConfigProvider {

    @Override
    public List<OssConfigInfo> loadOssConfigInfo(String clientType) {
        List<OssConfigInfo> configInfoList = get(clientType);
        for (OssConfigInfo configInfo : configInfoList) {
            validateOssConfigInfo(configInfo);
        }
        return configInfoList;
    }

    /**
     * 获取配置信息
     *
     * @param clientType 客户端类型
     * @return List<OssConfigInfo>
     */
    protected abstract List<OssConfigInfo> get(String clientType);
}
