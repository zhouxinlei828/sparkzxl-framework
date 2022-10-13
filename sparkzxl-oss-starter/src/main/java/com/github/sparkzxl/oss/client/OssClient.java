package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.SPI;

/**
 * description: Oss Client
 *
 * @author zhouxinlei
 * @since 2022-10-12 16:21:43
 */
@SPI
public interface OssClient<T> {

    /**
     * 初始化客户端
     *
     * @param configuration 客户端配置
     * @return OssClient
     */
    OssClient<T> init(Configuration configuration);

    /**
     * 获取client
     *
     * @return T
     */
    T get();

    /**
     * 获取配置
     *
     * @return Configuration
     */
    Configuration getConfiguration();

}
