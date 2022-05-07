package com.github.sparkzxl.oss.properties;

import com.github.sparkzxl.oss.properties.OssConfigInfo;

import java.util.List;
import java.util.function.Supplier;

/**
 * description:  A {@link Supplier} config of {@link OssConfigInfo} oss
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
}
