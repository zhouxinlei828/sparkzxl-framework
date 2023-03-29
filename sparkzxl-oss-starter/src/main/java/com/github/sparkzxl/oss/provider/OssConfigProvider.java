package com.github.sparkzxl.oss.provider;

import com.github.sparkzxl.oss.properties.Configuration;
import java.util.List;

/**
 * description:  A config provider of {@link Configuration} oss
 *
 * @author zhouxinlei
 * @since 2022-05-05 11:26:16
 */
public interface OssConfigProvider {

    /**
     * 加载客户端配置信息
     *
     * @param clientId 客户端类型
     * @return List<OssConfigInfo>
     */
    Configuration load(String clientId);


    /**
     * 加载客户端配置信息列表
     *
     * @return List<OssConfigInfo>
     */
    List<Configuration> loadConfigurationList();

}
