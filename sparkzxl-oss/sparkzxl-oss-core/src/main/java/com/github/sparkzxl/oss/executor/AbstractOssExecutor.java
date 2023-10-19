package com.github.sparkzxl.oss.executor;

import com.github.sparkzxl.oss.client.OssClient;
import com.github.sparkzxl.oss.properties.Configuration;

/**
 * description: 抽象oss执行器
 *
 * @author zhouxinlei
 * @since 2022-05-07 15:27:13
 */
public abstract class AbstractOssExecutor<T> implements OssExecutor {

    protected final OssClient<T> client;

    public AbstractOssExecutor(OssClient<T> client) {
        this.client = client;
    }

    /**
     * 获取当前线程客户端
     *
     * @return T
     */
    protected T obtainClient() {
        return client.get();
    }

    /**
     * 获取当前线程配置信息
     *
     * @return OssConfigInfo
     */
    protected Configuration obtainConfigInfo() {
        return client.getConfiguration();
    }
}
