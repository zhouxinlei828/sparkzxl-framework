package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.OssConfigInfo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * description: yaml 加载oss配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
public class YamlOssConfigProvider extends AbstractOssConfigProvider {

    private final List<OssConfigInfo> configInfoList;

    public YamlOssConfigProvider(List<OssConfigInfo> configInfoList) {
        for (OssConfigInfo configInfo : configInfoList) {
            if (StringUtils.isEmpty(configInfo.getClientType())) {
                throw new RuntimeException("Oss client clientType cannot be empty.");
            }
        }
        this.configInfoList = configInfoList;
    }

    @Override
    protected List<OssConfigInfo> get(String clientType) {
        return configInfoList.stream().filter(x -> StringUtils.equals(clientType, x.getClientType())).collect(Collectors.toList());
    }
}
