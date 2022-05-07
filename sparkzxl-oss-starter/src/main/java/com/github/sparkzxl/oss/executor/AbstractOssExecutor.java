package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.oss.properties.OssConfigInfo;
import com.github.sparkzxl.oss.provider.OssConfigProvider;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;
import java.util.Map;

/**
 * description: 抽象oss执行器
 *
 * @author zhouxinlei
 * @since 2022-05-07 15:27:13
 */
public abstract class AbstractOssExecutor<T> implements OssExecutor, InitializingBean {

    protected Map<String, T> clientMap;
    protected final OssConfigProvider ossConfigProvider;
    protected Map<String, OssConfigInfo> configInfoMap;

    public AbstractOssExecutor(OssConfigProvider ossConfigProvider) {
        this.ossConfigProvider = ossConfigProvider;
    }


    /**
     * 获取当前线程客户端
     *
     * @param clientId 客户端id
     * @return T
     */
    protected T obtainClient(String clientId) {
        if (StringUtils.isEmpty(clientId)) {
            ArgumentAssert.notNull(clientId, String.format("can not get client of %s", clientId));
        }
        final T ossClient = clientMap.get(clientId);
        ArgumentAssert.notNull(ossClient, String.format("can not get client of %s", ossClient));
        return ossClient;
    }

    /**
     * 获取当前线程配置信息
     *
     * @param clientId 客户端id
     * @return OssConfigInfo
     */
    protected OssConfigInfo obtainConfigInfo(String clientId) {
        if (StringUtils.isEmpty(clientId)) {
            ArgumentAssert.notNull(clientId, String.format("can not get client of %s", clientId));
        }
        final OssConfigInfo configInfo = configInfoMap.get(clientId);
        ArgumentAssert.notNull(configInfo, String.format("can not get configInfo of %s", configInfo));
        return configInfo;
    }

    @Override
    public void afterPropertiesSet() {
        this.clientMap = Maps.newHashMap();
        this.configInfoMap = Maps.newHashMap();
        List<OssConfigInfo> configInfoList = ossConfigProvider.loadOssConfigInfo(getClientType());
        for (OssConfigInfo configInfo : configInfoList) {
            clientMap.put(configInfo.getClientId(), initClient(configInfo));
            configInfoMap.put(configInfo.getClientId(), configInfo);
        }
    }

    /**
     * 初始化客户端实例
     *
     * @param configInfo oss配置信息
     * @return T
     */
    protected abstract T initClient(OssConfigInfo configInfo);
}
