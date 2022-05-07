package com.github.sparkzxl.oss.properties;

import com.github.sparkzxl.core.support.ExceptionAssert;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.oss.enums.StoreMode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * description: jdbc 加载oss 配置信息
 *
 * @author zhouxinlei
 * @since 2022-05-05 13:56:54
 */
@Getter
@Setter
@RequiredArgsConstructor
public class JdbcOssConfigProvider implements OssConfigProvider {

    private final OssProperties ossProperties;
    private final Function<String, List<OssConfigInfo>> function;

    @Override
    public List<OssConfigInfo> loadOssConfigInfo(String configType) {
        StoreMode storeMode = ossProperties.getStore();
        List<OssConfigInfo> configInfoList = null;
        if (storeMode.equals(StoreMode.JDBC)) {
            configInfoList = function.apply(configType);
        } else {
            ExceptionAssert.failure("wrong configuration information.");
        }
        ArgumentAssert.notNull(configInfoList, "No Oss configuration information found for configType：".concat(configType));
        for (OssConfigInfo configInfo : configInfoList) {
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
        return configInfoList;
    }
}
