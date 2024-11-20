package com.github.sparkzxl.oss.client;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.Join;
import lombok.Getter;

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
    public OSSClient getClient() {
        return this.client;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

}
