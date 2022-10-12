package com.github.sparkzxl.oss.client;

import com.github.sparkzxl.oss.properties.Configuration;
import com.github.sparkzxl.spi.Join;
import io.minio.MinioClient;

/**
 * description: MinioOssClient
 *
 * @author zhouxinlei
 * @since 2022-10-12 09:14:42
 */
@Join
public class MinioOssClient implements OssClient<MinioClient> {

    private MinioClient client;
    private Configuration configuration;

    public MinioOssClient() {
    }

    @Override
    public OssClient<MinioClient> init(Configuration configuration) {
        this.configuration = configuration;
        this.client = MinioClient.builder().endpoint(configuration.getEndpoint())
                .credentials(configuration.getAccessKey(), configuration.getSecretKey())
                .region(configuration.getRegion().getName())
                .build();
        return this;
    }

    @Override
    public MinioClient get() {
        return client;
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    public MinioClient getClient() {
        return client;
    }

    public void setClient(MinioClient client) {
        this.client = client;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
