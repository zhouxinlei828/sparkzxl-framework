package com.github.sparkzxl.oss.client;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.Join;

/**
 * description: MinioOssClient
 *
 * @author zhouxinlei
 * @since 2022-10-12 09:14:42
 */
@Join
public class AliyunOssClient implements OssClient<OSSClient> {

    private OSSClient client;
    private Configuration configuration;

    public AliyunOssClient() {

    }

    @Override
    public OssClient<OSSClient> init(Configuration configuration) {
        this.configuration = configuration;
        DefaultCredentialProvider defaultCredentialProvider = new DefaultCredentialProvider(
                configuration.getAccessKey(), configuration.getSecretKey());
        this.client = new OSSClient(configuration.getEndpoint(), defaultCredentialProvider, null);
        return this;
    }

    @Override
    public OSSClient get() {
        return client;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public OSSClient getClient() {
        return client;
    }

    public void setClient(OSSClient client) {
        this.client = client;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
