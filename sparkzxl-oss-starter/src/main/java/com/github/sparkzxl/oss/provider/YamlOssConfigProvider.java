package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * description: yaml 加载oss配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
public class YamlOssConfigProvider extends AbstractOssConfigProvider {

    private final List<Configuration> configList;

    public YamlOssConfigProvider(List<Configuration> configList) {
        for (Configuration configInfo : configList) {
            if (StringUtils.isEmpty(configInfo.getClientType())) {
                throw new RuntimeException("Oss client clientType cannot be empty.");
            }
        }
        this.configList = configList;
    }

    @Override
    public Configuration load(String clientId) {
        Optional<Configuration> optional = configList.stream().filter(config -> config.getClientId().equals(clientId)).findFirst();
        return optional.orElse(null);
    }

    @Override
    protected List<Configuration> list() {
        return configList;
    }
}
