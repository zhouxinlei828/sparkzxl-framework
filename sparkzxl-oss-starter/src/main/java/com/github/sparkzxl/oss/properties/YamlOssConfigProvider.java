package com.github.sparkzxl.oss.properties;

import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * description: yaml 加载oss 配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
@RequiredArgsConstructor
public class YamlOssConfigProvider implements OssConfigProvider {

    private final OssProperties ossProperties;

    @Override
    public List<OssConfigInfo> loadOssConfigInfo(String clientType) {
        Map<String, OssConfigInfo> provider = ossProperties.getProvider();
        List<OssConfigInfo> configInfoList = Lists.newArrayList();
        provider.forEach((key, value) -> {
            if (value.getClientType().equals(clientType)) {
                configInfoList.add(value);
            }
        });
        return configInfoList;
    }
}
