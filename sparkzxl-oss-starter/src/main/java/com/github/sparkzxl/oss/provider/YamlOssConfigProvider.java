package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.OssConfigInfo;
import com.github.sparkzxl.oss.properties.OssProperties;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * description: yaml 加载oss配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
@RequiredArgsConstructor
public class YamlOssConfigProvider extends AbstractOssConfigProvider {

    private final OssProperties ossProperties;

    @Override
    protected List<OssConfigInfo> get(String clientType) {
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
